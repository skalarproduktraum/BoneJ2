
package org.bonej.wrapperPlugins.wrapperUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.DefaultLinearAxis;
import net.imagej.table.GenericColumn;
import net.imagej.table.LongColumn;
import net.imagej.units.UnitService;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.DoubleType;

import org.bonej.wrapperPlugins.wrapperUtils.HyperstackUtils.Subspace;
import org.junit.AfterClass;
import org.junit.Test;

import ij.ImagePlus;
import ij.measure.Calibration;

/**
 * Unit tests for the {@link ResultUtils ResultUtils} class.
 *
 * @author Richard Domander
 */
public class ResultUtilsTest {

	private static final ImageJ IMAGE_J = new ImageJ();
	private static final UnitService unitService = IMAGE_J.context().getService(
		UnitService.class);

	@AfterClass
	public static void oneTimeTearDown() {
		IMAGE_J.context().dispose();
	}

	@Test
	public void testGetSizeDescription() throws Exception {
		final String[] expected = { "Size", "Area", "Volume", "Size" };
		final AxisType spatialAxis = Axes.get("Spatial", true);
		final DefaultLinearAxis axis = new DefaultLinearAxis(spatialAxis);
		final ImgPlus mockImage = mock(ImgPlus.class);
		when(mockImage.axis(anyInt())).thenReturn(axis);

		for (int i = 0; i < expected.length; i++) {
			final int dimensions = i + 1;
			when(mockImage.numDimensions()).thenReturn(dimensions);

			final String description = ResultUtils.getSizeDescription(mockImage);

			assertTrue("Size description is incorrect", expected[i].equals(
				description));
		}
	}

	@Test
	public void testGetExponent() throws Exception {
		final char[] expected = { '\u0000', '\u00B2', '\u00B3', '\u2074', '\u2075',
			'\u2076', '\u2077', '\u2078', '\u2079', '\u0000' };
		final AxisType spatialAxis = Axes.get("Spatial", true);
		final DefaultLinearAxis axis = new DefaultLinearAxis(spatialAxis);
		final ImgPlus<?> mockImage = mock(ImgPlus.class);
		when(mockImage.axis(anyInt())).thenReturn(axis);

		for (int i = 0; i < expected.length; i++) {
			final int dimensions = i + 1;
			when(mockImage.numDimensions()).thenReturn(dimensions);

			final char exponent = ResultUtils.getExponent(mockImage);

			assertEquals("Wrong exponent character", expected[i], exponent);
		}
	}

	@Test
	public void testGetUnitHeaderReturnEmptyIfImageNull() throws Exception {
		final String result = ResultUtils.getUnitHeader(null, unitService);

		assertTrue("Unit header should be empty", result.isEmpty());
	}

	@Test
	public void testGetUnitHeaderEmptyIfNoUnit() throws Exception {
		final DefaultLinearAxis axis = new DefaultLinearAxis(Axes.X);
		final Img<DoubleType> img = ArrayImgs.doubles(10);
		final ImgPlus<DoubleType> imgPlus = new ImgPlus<>(img, "Test image", axis);

		final String result = ResultUtils.getUnitHeader(imgPlus, unitService);

		assertTrue("Unit header should be empty", result.isEmpty());
	}

	@Test
	public void testGetUnitHeaderReturnEmptyIfDefaultUnitPixel()
		throws Exception
	{
		final DefaultLinearAxis axis = new DefaultLinearAxis(Axes.X, "pixel");
		final Img<DoubleType> img = ArrayImgs.doubles(10);
		final ImgPlus<DoubleType> imgPlus = new ImgPlus<>(img, "Test image", axis);

		final String result = ResultUtils.getUnitHeader(imgPlus, unitService);

		assertTrue("Unit header should be empty", result.isEmpty());
	}

	@Test
	public void testGetUnitHeaderReturnEmptyIfDefaultUnitUnit() throws Exception {
		final DefaultLinearAxis axis = new DefaultLinearAxis(Axes.X, "unit");
		final Img<DoubleType> img = ArrayImgs.doubles(10);
		final ImgPlus<DoubleType> imgPlus = new ImgPlus<>(img, "Test image", axis);

		final String result = ResultUtils.getUnitHeader(imgPlus, unitService);

		assertTrue("Unit header should be empty", result.isEmpty());
	}

	@Test
	public void testGetUnitHeader() throws Exception {
		final String unit = "mm";
		final char exponent = '³';
		final DefaultLinearAxis axis = new DefaultLinearAxis(Axes.X, unit);
		final Img<DoubleType> img = ArrayImgs.doubles(10);
		final ImgPlus<DoubleType> imgPlus = new ImgPlus<>(img, "Test image", axis);

		final String result = ResultUtils.getUnitHeader(imgPlus, unitService,
			exponent);

		assertEquals("Unexpected unit header", "(" + unit + exponent + ")", result);
	}

	@Test
	public void testCreateLabelColumnNullString() throws Exception {
		final GenericColumn column = ResultUtils.createLabelColumn(null, 3);

		assertNotNull(column);
		assertTrue("Column has incorrect values", column.stream().map(
			o -> (String) o).allMatch("-"::equals));
	}

	@Test
	public void testCreateLabelColumnEmptyString() throws Exception {
		final GenericColumn column = ResultUtils.createLabelColumn("", 3);

		assertTrue("Column has incorrect values", column.stream().map(
			o -> (String) o).allMatch("-"::equals));
	}

	@Test
	public void testCreateLabelNegativeRows() throws Exception {
		final GenericColumn column = ResultUtils.createLabelColumn("Test", -1);

		assertEquals(0, column.size());
	}

	@Test
	public void testCreateLabelColumn() throws Exception {
		final String label = "Test";
		final int rows = 10;

		final GenericColumn column = ResultUtils.createLabelColumn(label, rows);

		assertEquals("Column header is incorrect", "Label", column.getHeader());
		assertEquals("Wrong number of rows", rows, column.size());
		assertTrue("Column has incorrect values", column.stream().map(
			o -> (String) o).allMatch(label::equals));
	}

	@Test
    public void testToConventionalIndexNullType() throws Exception {
        assertEquals(0, ResultUtils.toConventionalIndex(null, 0));
    }

	@Test
	public void testToConventionalIndex() throws Exception {
		final Stream<AxisType> types = Stream.of(Axes.X, Axes.Y, Axes.Z,
			Axes.CHANNEL, Axes.TIME);
		final Iterator<Long> expectedIndices = Stream.of(0L, 0L, 1L, 1L, 1L)
			.iterator();

		types.map(type -> ResultUtils.toConventionalIndex(type, 0)).forEach(
			i -> assertEquals(expectedIndices.next().longValue(), i.longValue()));
	}

	@Test
	public void testGetUnitHeaderImagePlusReturnsEmptyIfUnitEmpty()
		throws Exception
	{
		final ImagePlus imagePlus = new ImagePlus();
		final Calibration calibration = new Calibration();
		calibration.setUnit("");
		imagePlus.setCalibration(calibration);

		final String unitHeader = ResultUtils.getUnitHeader(imagePlus);

		assertEquals("", unitHeader);
	}

	@Test
	public void testGetUnitHeaderImagePlusReturnsEmptyIfDefaultUnit()
		throws Exception
	{
		final ImagePlus imagePlus = new ImagePlus();
		final Calibration calibration = new Calibration();
		imagePlus.setCalibration(calibration);

		final String unitHeader = ResultUtils.getUnitHeader(imagePlus);

		assertEquals("", unitHeader);
	}

	@Test
	public void testGetUnitHeaderImagePlusReturnsEmptyIfUnitUnit()
		throws Exception
	{
		final ImagePlus imagePlus = new ImagePlus();
		final Calibration calibration = new Calibration();
		calibration.setUnit("unit");
		imagePlus.setCalibration(calibration);

		final String unitHeader = ResultUtils.getUnitHeader(imagePlus);

		assertEquals("", unitHeader);
	}

	@Test
	public void testGetUnitHeaderImagePlus() throws Exception {
		final ImagePlus imagePlus = new ImagePlus();
		final Calibration calibration = new Calibration();
        final String unit = "mm";
        calibration.setUnit(unit);
		imagePlus.setCalibration(calibration);

		final String unitHeader = ResultUtils.getUnitHeader(imagePlus);

		assertEquals("(" + unit + ")", unitHeader);
	}
}
