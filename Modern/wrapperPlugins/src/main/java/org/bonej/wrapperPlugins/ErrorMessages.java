package org.bonej.wrapperPlugins;

/**
 * Common error messages displayed for the user
 *
 * @author Richard Domander 
 */
public class ErrorMessages {
    public static final String NOT_3D_IMAGE = "Need a 3D image";
    public static final String NOT_2D_OR_3D_IMAGE = "Need a 2D or 3D image";
    public static final String HAS_TIME_DIMENSIONS = "Image cannot have time axis";
    public static final String HAS_CHANNEL_DIMENSIONS = "Image cannot be composite";

    public static final String NOT_8_BIT_BINARY_IMAGE = "Need an 8-bit binary image";
    public static final String CANNOT_CONVERT_TO_IMAGE_PLUS =
            "The image is incompatible with this plugin (cannot convert to ImagePlus)";
    public static final String NO_IMAGE_OPEN = "No image open";
    public static final String NO_SKELETONS = "Image contains no skeletons";

    private ErrorMessages() {}
}
