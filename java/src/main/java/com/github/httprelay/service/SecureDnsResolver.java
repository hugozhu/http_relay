package com.github.httprelay.service;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * todo: check IP is not intranet
 */
public class SecureDnsResolver implements DnsResolver {
    NetDB netdb;

    public SecureDnsResolver() {
        netdb = new NetDB();
        try {
            netdb.addIP("127.0.0.1", 32);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to set up SecureDnsResolver",e);
        }
    }

    @Override
    public InetAddress[] resolve(String hostname) throws UnknownHostException{
        InetAddress[] addresses  = new InetAddress[]{InetAddress.getByName(hostname)};
        for (InetAddress addr: addresses) {
            if(addr.isAnyLocalAddress()||addr.isLoopbackAddress()) {
                throw new IllegalStateException(addr+" is not a valid api address");
            }
            if (netdb.isInternalIP(addr.getHostAddress())) {
                throw new IllegalStateException(addr+" is internal address");
            }
        }
        return addresses;
    }

    public static void main(String ... args) throws Exception {
        SecureDnsResolver resolver = new SecureDnsResolver();
        resolver.resolve("localhost");
    }
}

class NetDB {
    private Trie trie;

    public void addIP(String ip,int value) throws Exception {
        trie.addEntry(ip,value);
    }

    private char[] ip_to_chars(String ip) {
        char[] parts = new char[]{'\0','\0','\0','\0'};
        int p1 = 0;
        int p2 = ip.indexOf(".");
        parts[0] = (char) Integer.parseInt(ip.substring(p1,p2));
        p1 = p2+1;
        p2 = ip.indexOf(".",p1);
        if (p2==-1) {
            parts[1] = (char) Integer.parseInt(ip.substring(p1));
            return parts;
        }
        parts[1] = (char) Integer.parseInt(ip.substring(p1,p2));
        p1 = p2+1;
        p2 = ip.indexOf(".",p1);
        if (p2==-1) {
            parts[2] = (char) Integer.parseInt(ip.substring(p1));
            return parts;
        }
        parts[2] = (char) Integer.parseInt(ip.substring(p1,p2));
        p1 = p2+1;
        parts[3] = (char) Integer.parseInt(ip.substring(p1));
        return parts;
    }

    public NetDB() {
        trie = new Trie(10);
    }

    private boolean check_slow(String ip) {
        char[] parts = ip_to_chars(ip);
        if (trie.getEntry(parts) == 32) {
            return true;
        }
        parts[3]='\0';
        if (trie.getEntry(parts) == 24) {
            return true;
        }
        parts[2]='\0';
        if (trie.getEntry(parts) == 16) {
            return true;
        }
        return false;
    }

    public boolean isInternalIP(String ip) {
        if (trie.getEntry(ip) == 32) {
            return true;
        }
        int lastPos = ip.lastIndexOf(".");
        if (lastPos==-1)
            return false;
        if (trie.getEntry(ip.substring(0,lastPos)) == 24) {
            return true;
        }
        lastPos = ip.lastIndexOf(".",lastPos-1);
        if (lastPos==-1)
            return false;
        if (trie.getEntry(ip.substring(0,lastPos)) == 16) {
            return true;
        }
        return false;
    }
}

class Trie {
    /// factory to create nodes
    private TrieNodeFactory factory;

    /**
     * @brief create the trie with a specific number of node at the
     * begining.
     */
    public Trie(int initialNbNodes) {
        factory = new TrieNodeFactory(initialNbNodes);
        factory.newNode(); // initial node
    }

    /**
     * @brief add an String entry in trie. Value must be different than -1
     * (-1 is reserved when there is no match found)
     */
    public void addEntry(String str, int value) throws Exception {
        int currentNode = _addEntry(str);

        // Set the value on the last node
        if (currentNode >= 0 && getNodeValue(currentNode) != -1)
            throw new Exception("The word is already in trie");
        _setNodeValue(currentNode, value);
    }

    /**
     * @brief set an String entry in trie. Value must be different than -1
     * (-1 is reserved when there is no match found)
     */
    public void setEntry(String str, int value) throws Exception {
        int currentNode = _addEntry(str);

        // Set the value on the last node
        _setNodeValue(currentNode, value);
    }

    public int getEntry(char[] chars) {
        int currentNode = 0, strPos = 0;
        boolean found = true;

        // Look for the part of the word which is in Trie
        while (found && strPos < chars.length) {
            found = false;
            currentNode = _lookingSubNode(currentNode, chars[strPos]);
            if (currentNode >= 0) {
                found = true;
                ++strPos;
            }
        }

        if (currentNode > 0 && strPos == chars.length) // The word is complet in the automaton
            return getNodeValue(currentNode);
        return -1;
    }

    /**
     * @brief get integer associef to a string, return -1 if entry is not found.
     */
    public int getEntry(String str) {
        int currentNode = 0, strPos = 0;
        boolean found = true;

        // Look for the part of the word which is in Trie
        while (found && strPos < str.length()) {
            found = false;
            currentNode = _lookingSubNode(currentNode, str.charAt(strPos));
            if (currentNode >= 0) {
                found = true;
                ++strPos;
            }
        }

        if (currentNode > 0 && strPos == str.length()) // The word is complet in the automaton
            return getNodeValue(currentNode);
        return -1;
    }

    /**
     * display the whole content of trie
     */
    public void display() {
        _display(0, (char) 0, -2);
    }

    /**
     * accessor to iterator over the trie. the initial node is 0.
     * return -1 when there is no sub node
     */
    public int getFirstSubNode(int node) {
        return factory.getFirstSubNode(node);
    }

    /**
     * accessor to iterator over the trie. the initial node is 0.
     */
    public char getFirstSubNodeLabel(int node) {
        return factory.getFirstSubNodeLabel(node);
    }

    /**
     * accessor to iterator over the trie. the initial node is 0.
     * return -1 when there is no sub node
     */
    public int getBrother(int node) {
        return factory.getBrother(node);
    }

    /**
     * accessor to iterator over the trie. the initial node is 0.
     */
    public char getBrotherLabel(int node) {
        return factory.getBrotherLabel(node);
    }

    /**
     * accessor to iterator over the trie. the initial node is 0
     */
    public int getNodeValue(int node) {
        return factory.getValue(node);
    }

    /**
     * display content of a node and his sub nodes
     */
    protected void _display(int node, char label, int offset) {
        int firstSubNode = factory.getFirstSubNode(node);
        char firstSubNodeLabel = factory.getFirstSubNodeLabel(node);
        int brother = factory.getBrother(node);
        char brotherLabel = factory.getBrotherLabel(node);

        if (label != 0) {
            for (int i = 0; i < offset; ++i)
                System.out.print(" ");
            System.out.println("label[" + label + "]");
        }
        if (firstSubNode >= 0)
            _display(firstSubNode, firstSubNodeLabel, offset + 2);
        if (brother >= 0)
            _display(brother, brotherLabel, offset);
    }

    /**
     * add an element in sub nodes
     */
    protected void _addSubNode(int node, char chr, int newNode) {
        int firstSubNode = factory.getFirstSubNode(node);
        char firstSubNodeLabel = factory.getFirstSubNodeLabel(node);

        if (firstSubNode < 0 || firstSubNodeLabel > chr) {
            factory.setBrother(newNode, firstSubNode, firstSubNodeLabel);
            factory.setFirstSubNode(node, newNode, chr);
        } else
            _addBrother(firstSubNode, chr, newNode);
    }

    /**
     * add an element in list of brother
     */
    protected void _addBrother(int node, char chr, int newNode) {
        int brother = factory.getBrother(node);
        char brotherLabel = factory.getBrotherLabel(node);

        if (brother < 0 || brotherLabel > chr) {
            factory.setBrother(newNode, brother, brotherLabel);
            factory.setBrother(node, newNode, chr);
        } else
            _addBrother(brother, chr, newNode);
    }

    /**
     * Looking for a sub node of node accessible with chr
     */
    protected int _lookingSubNode(int node, char chr) {
        int firstSubNode = factory.getFirstSubNode(node);
        char firstSubNodeLabel = factory.getFirstSubNodeLabel(node);

        if (firstSubNode >= 0 && firstSubNodeLabel <= chr) {
            if (firstSubNodeLabel == chr)
                return firstSubNode;
            return _lookingBrother(firstSubNode, chr);
        }
        return -1;
    }

    /**
     * Looking for a node accessible with chr in list of brother
     * of node
     */
    protected int _lookingBrother(int node, char chr) {
        int brother = factory.getBrother(node);
        char brotherLabel = factory.getBrotherLabel(node);

        if (brother >= 0 && brotherLabel <= chr) {
            if (brotherLabel == chr)
                return brother;
            return _lookingBrother(brother, chr);
        }
        return -1;
    }

    /// set value of node

    protected void _setNodeValue(int node, int value) {
        factory.setValue(node, value);
    }

    /**
     * @brief add an String entry in trie.
     */
    protected int _addEntry(String str) throws Exception {
        int currentNode = 0, previousNode = 0, strPos = 0;
        boolean found = true;

        // Look for the part of the word which is in Trie
        while (found && strPos < str.length()) {
            found = false;
            previousNode = currentNode;
            currentNode = _lookingSubNode(currentNode, str.charAt(strPos));
            if (currentNode >= 0) {
                found = true;
                ++strPos;
            }
        }
        // Add part of the word which is not in Trie
        if (currentNode < 0 || strPos != str.length()) {
            currentNode = previousNode;
            while (strPos < str.length()) {
                int newNode = factory.newNode();
                _addSubNode(currentNode, str.charAt(strPos), newNode);
                currentNode = newNode;
                ++strPos;
            }
        }
        if (currentNode < 0)
            throw new Exception("Buggy Trie");
        return currentNode;
    }
}

/**
 * @brief this class represent The creation of trie Node.
 * Each node contains 5 elements :
 * <ul>
 * <li>a link to his first subnode with a label (firstSubNode and firstSubNodeLabel)</li>
 * <li>a link to his brother with a label (brother and brotherLabel), brother is a
 * node which has the same father as current node</li>
 * <li>a integer value</li>
 * </ul>
 * Append of trie node by Factory is done in amortized constant time. Since
 * this will sometimes cause a memory reallocation, this implementation
 * allocate twice as much memory every time the vector is resized.
 */
class TrieNodeFactory {
    /// store the link to the first subnode for each node
    private int firstSubNode[];
    /// store the label to go to first subnode for each node
    private char firstSubNodeLabel[];
    /// store the link to a brother
    private int brother[];
    /// store the label to go to brother
    private char brotherLabel[];
    /// store the value associed to each trie node
    private int values[];
    /// store the number of node allocated
    private int size;
    /// store the number of node used
    private int nbNodes;

    /**
     * @brief create an instance of this factory and start allocating
     * nbNodeToAllocate nodes
     */
    public TrieNodeFactory(int nbNodeToAllocate) {
        size = nbNodeToAllocate;
        firstSubNode = new int[size];
        firstSubNodeLabel = new char[size];
        brother = new int[size];
        brotherLabel = new char[size];
        values = new int[size];
        nbNodes = 0;
    }

    /**
     * @brief resize a array of int. existing elements are copied using
     * System.arraycopy().
     */
    protected int[] resizeIntArray(int orig[], int newSize) {
        int newIntArray[] = new int[newSize];
        System.arraycopy(orig, 0, newIntArray, 0, orig.length);
        return newIntArray;
    }

    /**
     * @brief resize a array of char. existing elements are copied using
     * System.arraycopy().
     */
    protected char[] resizeCharArray(char orig[], int newSize) {
        char newCharArray[] = new char[newSize];
        System.arraycopy(orig, 0, newCharArray, 0, orig.length);
        return newCharArray;
    }

    /**
     * @brief allocate a new node in trie and return the index used. If there
     * is no allocated node available, existing arrays with twice as much size as
     * previous size. return value are between range [1..n], 0 is reserved
     * for sentinel.
     */
    public int newNode() {
        if (nbNodes == size) {
            size *= 2;
            // resize all existing array
            firstSubNode = resizeIntArray(firstSubNode, size);
            firstSubNodeLabel = resizeCharArray(firstSubNodeLabel, size);
            brother = resizeIntArray(brother, size * 2);
            brotherLabel = resizeCharArray(brotherLabel, size);
            values = resizeIntArray(values, size);
        }
        /// initial new node
        firstSubNode[nbNodes] = -1;
        brother[nbNodes] = -1;
        values[nbNodes] = -1;
        ++nbNodes;
        return nbNodes - 1;
    }

    /**
     * @brief return the first sub node for a specific node. Node must be
     * in the range [0..n-1], the return value is into range [0..n-1] and is
     * equal to -1 if there is no subnode.
     */
    public int getFirstSubNode(int node) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        return firstSubNode[node];
    }

    /**
     * @brief return the label for first sub node for a specific node. Node must be
     * in the range [0..n-1].
     */
    public char getFirstSubNodeLabel(int node) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        return firstSubNodeLabel[node];
    }

    /**
     * @brief return the first brother for a specific node. Node must be
     * in the range [0..n-1], the return value is into range [0..n-1] and is
     * equal to -1 if there is no subnode.
     */
    public int getBrother(int node) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        return brother[node];
    }

    /**
     * @brief return the label for brother for a specific node. Node must be
     * in the range [0..n-1].
     */
    public char getBrotherLabel(int node) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        return brotherLabel[node];
    }

    /**
     * @brief return the value for a specific node. Node must be
     * in the range [0..n-1].
     */
    public int getValue(int node) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        return values[node];
    }

    /**
     * @brief set the value for a specific node. Node must be
     * in the range [0..n-1].
     */
    public void setValue(int node, int value) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        values[node] = value;
    }

    /**
     * @brief set the value of first sub node for a specific node. Node must be
     * in the range [0..n-1].
     */
    public void setFirstSubNode(int node, int dest, char destLabel) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        firstSubNode[node] = dest;
        firstSubNodeLabel[node] = destLabel;
    }

    /**
     * @brief set the value of brother for a specific node. Node must be
     * in the range [0..n-1].
     */
    public void setBrother(int node, int dest, char destLabel) throws IndexOutOfBoundsException {
        if (node < 0 || node >= nbNodes)
            throw new IndexOutOfBoundsException("invalid index for first sub node accessor");
        brother[node] = dest;
        brotherLabel[node] = destLabel;
    }

}
