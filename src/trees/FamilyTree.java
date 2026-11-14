
package trees;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FamilyTree {

    // BONUS: Make TreeNode generic so it can store any data type.
    private static class TreeNode<T> {
        // BONUS: Change name from String to generic type T.
        private T data;
        private TreeNode<T> parent;
        private ArrayList<TreeNode<T>> children;

        TreeNode(T data) {
            this.data = data;
            children = new ArrayList<>();
        }

        // BONUS: Rename getName() to getData().
        T getData() {
            return data;
        }

        void addChild(TreeNode<T> childNode) {
            // Add childNode to this node's children list. Also
            // set childNode's parent to this node.
            children.add(childNode);
            childNode.parent = this;
        }

        // Searches subtree at this node for a node
        // with the given name. Returns the node, or null if not found.
        // BONUS: Use generic type T instead of String.
        TreeNode<T> getNodeWithName(T targetName) {
            // Does this node have the target name?
            if (Objects.equals(data, targetName))
                return this;

            // No, recurse. Check all children of this node.
            for (TreeNode<T> child: children)
            {
                // If child.getNodeWithName(targetName) returns a non-null node,
                // then that's the node we're looking for. Return it.
                TreeNode<T> found = child.getNodeWithName(targetName);
                if (found != null) {
                    return found;
                }
            }

            // Not found anywhere.
            return null;
        }

        // Returns a list of ancestors of this TreeNode, starting with this node’s
        // parent and
        // ending with the root. Order is from recent to ancient.
        ArrayList<TreeNode<T>> collectAncestorsToList() {
            ArrayList<TreeNode<T>> ancestors = new ArrayList<>();

            // ????? Collect ancestors of this TreeNode into the array list. HINT: going up
            // the nodes of a tree is like traversing a linked list. If that isn’t clear,
            // draw a tree, mark any leaf node, and then mark its ancestors in order from
            // recent to ancient. Expect a question about this on the final exam.
            // BONUS: Update references to use generic TreeNode<T>.
            TreeNode<T> current = this.parent;
            while (current != null) {
                ancestors.add(current);
                current = current.parent;
            }

            return ancestors;
        }

        public String toString() {
            return toStringWithIndent("");
        }

        private String toStringWithIndent(String indent) {
            // BONUS: Convert generic data to a String for printing.
            String s = indent + String.valueOf(data) + "\n";
            indent += "  ";
            for (TreeNode<T> childNode : children)
                s += childNode.toStringWithIndent(indent);
            return s;
        }
    }

    // BONUS: FamilyTree stores String data in its generic nodes.
    private TreeNode<String> root;

    //
    // Displays a file browser so that user can select the family tree file.
    //
    public FamilyTree() throws IOException, TreeException {
        // User chooses input file. This block doesn't need any work.
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Family tree text files", "txt");
        File dirf = new File("data");
        if (!dirf.exists()) dirf = new File(".");

        JFileChooser chooser = new JFileChooser(dirf);
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(1);
        File treeFile = chooser.getSelectedFile();

        // Parse the input file. Create a FileReader that reads treeFile. Create a BufferedReader
        // that reads from the FileReader.
        FileReader fr = new FileReader(treeFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null)
            addLine(line);
        br.close();
        fr.close();
    }

    //
    // Line format is "parent:child1,child2 ..."
    // Throws TreeException if line is illegal.
    //
    private void addLine(String line) throws TreeException
    {
        // Extract parent and array of children.
        int colonIndex = line.indexOf(':');
        if (colonIndex < 0)
            throw new TreeException("Missing ':' in line: " + line);
        String parent = line.substring(0, colonIndex);
        String childrenString = line.substring(colonIndex + 1);
        String[] childrenArray = childrenString.split(",");

        // Find parent node. If root is null then the tree is empty and the
        // parent node must be constructed. Otherwise the parent node should be
        // somewhere in the tree.
        // BONUS: Use TreeNode<String> instead of raw TreeNode.
        TreeNode<String> parentNode;
        if (root == null)
            parentNode = root = new TreeNode<>(parent);
        else
        {
            parentNode = root.getNodeWithName(parent);
            if (parentNode == null)
                throw new TreeException("Parent node '" + parent + "' not found in line: " + line);
        }

        // Add child nodes to parentNode.
        // BONUS: Create generic nodes using the diamond operator.
        for (String child : childrenArray)
            parentNode.addChild(new TreeNode<>(child));
    }

    // Returns the "deepest" node that is an ancestor of the node named name1, and
    // also is an
    // ancestor of the node named name2.
    //
    // "Depth" of a node is the "distance" between that node and the root. The depth
    // of the root is 0. The
    // depth of the root's immediate children is 1, and so on.
    //
    TreeNode<String> getMostRecentCommonAncestor(String name1, String name2) throws TreeException
    {
        // Get nodes for input names.
        if (root == null)
            throw new TreeException("Cannot find a common ancestor in an empty family tree.");

        // BONUS: Use TreeNode<String> for the family members.
        TreeNode<String> node1 = root.getNodeWithName(name1);
        if (node1 == null)
            throw new TreeException("Node '" + name1 + "' not found in the family tree.");

        TreeNode<String> node2 = root.getNodeWithName(name2);
        if (node2 == null)
            throw new TreeException("Node '" + name2 + "' not found in the family tree.");

        // Get ancestors of node1 and node2.
        ArrayList<TreeNode<String>> ancestorsOf1 = node1.collectAncestorsToList();
        ArrayList<TreeNode<String>> ancestorsOf2 = node2.collectAncestorsToList();

        // Check members of ancestorsOf1 in order until you find a node that is also
        // an ancestor of 2.
        for (TreeNode<String> n1: ancestorsOf1)
            if (ancestorsOf2.contains(n1))
                return n1;

        // No common ancestor.
        return null;
    }

    public String toString() {
        return "Family Tree:\n\n" + root;
    }

    public static void main(String[] args) {
        try {
            FamilyTree tree = new FamilyTree();
            System.out.println("Tree:\n" + tree + "\n**************\n");

            // BONUS: Removed the Bilbo and Frodo ancestor lookup
            // so this program can also run with other family tree files.

        } catch (IOException x) {
            System.out.println("IO trouble: " + x.getMessage());
        } catch (TreeException x) {
            System.out.println("Input file trouble: " + x.getMessage());
        }
    }
}
