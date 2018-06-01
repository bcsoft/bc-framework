package cn.bc.web.ui.tree;

import cn.bc.web.ui.html.tree.Tree;
import cn.bc.web.ui.html.tree.TreeNode;
import org.junit.Test;

public class TreeTest {
  @Test
  public void test01_treeNode_leaf() {
    TreeNode node = new TreeNode("1", "叶子节点");
    node.setLeaf(true);
    System.out.println(node);
  }

  @Test
  public void test02_treeNode_folder_open() {
    TreeNode node = new TreeNode("2", "父节点(展开状态)");
    node.setLeaf(false);
    node.setOpen(true);
    System.out.println(node);
  }

  @Test
  public void test03_treeNode_folder_collapsed() {
    TreeNode node = new TreeNode("3", "父节点(折叠状态)");
    node.setLeaf(false);
    node.setOpen(false);
    System.out.println(node);
  }

  @Test
  public void test04_treeNode_subNodes() {
    TreeNode node = new TreeNode("4", "父节点(展开状态)-带子节点");
    node.setLeaf(false);
    node.setOpen(true);

    TreeNode subNode = new TreeNode("4-1", "子节点1");
    subNode.setLeaf(true);
    node.addSubNode(subNode);

    subNode = new TreeNode("4-2", "子节点2");
    subNode.setLeaf(true);
    subNode.setIcon("sc2");
    node.addSubNode(subNode);

    System.out.println(node);
  }

  @Test
  public void test04_tree() {
    Tree tree = new Tree("0", "根节点(默认展开状态)");

    TreeNode node = new TreeNode("1", "父节点1");
    node.setLeaf(false);
    node.setOpen(true);
    tree.addSubNode(node);

    TreeNode subNode = new TreeNode("1-1", "子节点11");
    subNode.setLeaf(true);
    node.addSubNode(subNode);

    node = new TreeNode("2", "叶子节点");
    node.setLeaf(true);
    tree.addSubNode(node);

    System.out.println(tree);
  }
}
