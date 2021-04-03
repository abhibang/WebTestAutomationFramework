package com.webtestautomationframework.utils.gifcreation;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.IOException;

public class Giffer {

    protected ImageWriter writer;

    protected ImageWriteParam params;

    protected IIOMetadata metadata;

    public Giffer(ImageOutputStream out, int imageType, int delay, boolean loop) throws IOException {
        writer = ImageIO.getImageWritersBySuffix("gif").next();
        params = writer.getDefaultWriteParam();

        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, params);

        configureRootMetadata(delay, loop);

        writer.setOutput(out);
        writer.prepareWriteSequence(null);
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }

    private void configureRootMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
        String metaFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);

        getNode(root, "GraphicControlExtension").setAttribute("delayTime", Long.toString(delay / 10L));
        getNode(root, "GraphicControlExtension").setAttribute("userInputFlag", "FALSE");
        getNode(root, "GraphicControlExtension").setAttribute("disposalMethod", "none");
        getNode(root, "GraphicControlExtension").setAttribute("transparentColorFlag", "FALSE");
        getNode(root, "GraphicControlExtension").setAttribute("transparentColorIndex", "0");

        if (loop) {
            IIOMetadataNode applicationExtensionNode = new IIOMetadataNode("ApplicationExtension");
            applicationExtensionNode.setAttribute("applicationID", "NETSCAPE");
            applicationExtensionNode.setAttribute("authenticationCode", "2.0");
            applicationExtensionNode.setUserObject(new byte[]{(byte) 0x1, (byte) (0xFF), (byte) (0xFF)});
            getNode(root, "ApplicationExtensions").appendChild(applicationExtensionNode);
        }

        metadata.setFromTree(metaFormatName, root);
    }

    public void writeToSequence(RenderedImage img) throws IOException {
        writer.writeToSequence(new IIOImage(img, null, metadata), params);
    }

    public void close() throws IOException {
        writer.endWriteSequence();
    }
}
