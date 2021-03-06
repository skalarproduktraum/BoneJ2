/*
BSD 2-Clause License
Copyright (c) 2018, Michael Doube, Richard Domander, Alessandro Felder
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.bonej.util;

import ij.ImagePlus;
import ij.measure.Calibration;

public final class ThresholdGuesser {

	private static final double airHU = -1000;

	private ThresholdGuesser() {}

	/**
	 * Set up default thresholds and report them to the user as HU values if the
	 * image has HU calibration or plain values if not. Used as a first guess for
	 * dialogs that have to handle both HU and uncalibrated images.
	 *
	 * @param imp an image.
	 * @return double[2] containing minimum and maximum thresholds
	 */
	public static double[] setDefaultThreshold(final ImagePlus imp) {
		final Calibration cal = imp.getCalibration();
		double min;
		double max;
		if (ImageCheck.huCalibrated(imp)) {
			// default bone thresholds are 0 and 4000 HU
			min = airHU + 1000;
			max = airHU + 5000;
			return new double[] { min, max };
		}
		// set some sensible thresholding defaults
		final int[] histogram = StackStats.getStackHistogram(imp);
		final int histoLength = histogram.length;
		int histoMax = histoLength - 1;
		for (int i = histoLength - 1; i >= 0; i--) {
			if (histogram[i] > 0) {
				histoMax = i;
				break;
			}
		}
		min = imp.getProcessor().getAutoThreshold(histogram);
		max = histoMax;
		if (cal.isSigned16Bit() && cal.getCValue(0) == 0) {
			min += Short.MIN_VALUE;
			max += Short.MIN_VALUE;
		}
		return new double[] { min, max };
	}
}
