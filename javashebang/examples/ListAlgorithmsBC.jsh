#!/home/rattom/bin/javashebang
#START_JSH
#JAVA_HOME=${JAVA_HOME}
#JAVA_OPTS=-Xms256m -Xmx1024m
#JAVA_CLASSPATH=${JAVA_LIBS_DIR}/bouncycastle.org/1.5.3/bcprov-ext-jdk15on-153.jar
#END_JSH

import java.awt.Dimension;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class ListAlgorithmsBC extends JFrame {
  private void getNodes(DefaultMutableTreeNode providerNode, Provider provider,
      Set<Provider.Service> used, String title, String target) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(title);
    for (Provider.Service service : provider.getServices()) {
      if (!used.contains(service) && target.equalsIgnoreCase(service.getType())) {
        used.add(service);
        DefaultMutableTreeNode algNode = new DefaultMutableTreeNode(service.getAlgorithm());
        node.add(algNode);
        algNode.add(new DefaultMutableTreeNode("class : " + service.getClassName()));
      }
    }
    if (node.getChildCount() != 0) {
      providerNode.add(node);
    }
  }

  private ListAlgorithmsBC() {
    super("JCE Algorithms");
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Providers");
    DefaultTreeModel treeModel = new DefaultTreeModel(root);
    for (Provider provider : java.security.Security.getProviders()) {
      DefaultMutableTreeNode providerNode = new DefaultMutableTreeNode(provider);
      root.add(providerNode);
      Set<Provider.Service> used = new HashSet<Provider.Service>();

      getNodes(providerNode, provider, used, "Cipher", "cipher");
      getNodes(providerNode, provider, used, "Key Agreement", "keyagreement");
      getNodes(providerNode, provider, used, "Key Generator", "keygenerator");
      getNodes(providerNode, provider, used, "Key Pair Generator", "keypairgenerator");
      getNodes(providerNode, provider, used, "Key Factory", "keyfactory");
      getNodes(providerNode, provider, used, "Secret Key Factory", "secretkeyfactory");
      getNodes(providerNode, provider, used, "Mac", "mac");
      getNodes(providerNode, provider, used, "Message Digest", "messagedigest");
      getNodes(providerNode, provider, used, "Signature", "signature");
      getNodes(providerNode, provider, used, "Algorithm Paramater", "algorithmparameters");
      getNodes(providerNode, provider, used, "Algorithm Paramater Generator",
          "algorithmparametergenerator");
      getNodes(providerNode, provider, used, "Key Store", "keystore");
      getNodes(providerNode, provider, used, "Secure Random", "securerandom");
      getNodes(providerNode, provider, used, "Certificate Factory", "certificatefactory");
      getNodes(providerNode, provider, used, "Certificate Store", "certstore");
      getNodes(providerNode, provider, used, "Key Manager Factory", "KeyManagerFactory");
      getNodes(providerNode, provider, used, "Trust Manager Factory", "TrustManagerFactory");
      getNodes(providerNode, provider, used, "SSL Context", "SSLContext");
      getNodes(providerNode, provider, used, "Sasl Server Factory", "SaslServerFactory");
      getNodes(providerNode, provider, used, "Sasl Client Factory", "SaslClientFactory");
      {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Other");
        for (Provider.Service service : provider.getServices()) {
          if (!used.contains(service)) {
            DefaultMutableTreeNode serviceNode = new DefaultMutableTreeNode(service.getType()
                + " : " + service.getAlgorithm());
            node.add(serviceNode);
            serviceNode.add(new DefaultMutableTreeNode("class : " + service.getClassName()));
          }
        }
        if (node.getChildCount() != 0)
          providerNode.add(node);
      }
    }

    JTree tree = new JTree(treeModel);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setEditable(false);
    JScrollPane pane = new JScrollPane(tree);
    pane.setPreferredSize(new Dimension(200, 200));

    getContentPane().add(pane);

    pack();
  }

  public static void main(String[] args) {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    new ListAlgorithmsBC().setVisible(true);
  }
}
