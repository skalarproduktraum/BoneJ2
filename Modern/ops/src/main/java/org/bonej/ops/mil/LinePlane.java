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

package org.bonej.ops.mil;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.imagej.ops.special.hybrid.BinaryHybridCFI1;
import net.imglib2.Interval;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * A class that describes a set of parallel lines normal to a plane. The plane
 * passes through an interval.
 *
 * @author Richard Domander
 */
class LinePlane {

	private final double size;
	private final Vector3dc translation;
	private final Vector3dc centroid;
	private final Vector3dc direction;
	private final Random random = new Random();
	private final Quaterniondc rotation;
	private final BinaryHybridCFI1<Vector3d, Quaterniondc, Vector3d> rotateOp;

	/**
	 * Creates an instance of LinePlane, and initializes it for generating lines.
	 *
	 * @param interval a discrete interval through which the lines pass.
	 * @param rotation the direction of the lines through the interval.
	 * @param rotateOp the Op used to rotate the origin points and direction.
	 * @param <I> type of the interval.
	 */
	<I extends Interval> LinePlane(final I interval, final Quaterniondc rotation,
		final BinaryHybridCFI1<Vector3d, Quaterniondc, Vector3d> rotateOp)
	{
		size = findPlaneSize(interval);
		translation = new Vector3d(-size * 0.5, -size * 0.5, 0.0);
		centroid = findCentroid(interval);
		this.rotateOp = rotateOp;
		this.rotation = rotation;
		direction = createDirection();
	}

	// region -- Helper methods --
	private Vector3dc createDirection() {
		final Vector3d direction = new Vector3d(0, 0, 1);
		rotateOp.mutate1(direction, rotation);
		return direction;
	}

	private Vector3dc createOrigin(final double t, final double u) {
		final double x = t * size;
		final double y = u * size;
		final Vector3d origin = new Vector3d(x, y, 0);
		origin.add(translation);
		rotateOp.mutate1(origin, rotation);
		origin.add(centroid);
		return origin;
	}

	private static <I extends Interval> Vector3dc findCentroid(final I interval) {
		final double[] coordinates = IntStream.range(0, 3).mapToDouble(d -> interval
			.max(d) + 1 - interval.min(d)).map(d -> d / 2.0).toArray();
		return new Vector3d(coordinates[0], coordinates[1], coordinates[2]);
	}

	private static <I extends Interval> double findPlaneSize(final I interval) {
		final long sqSum = LongStream.of(interval.dimension(0), interval.dimension(
			1), interval.dimension(2)).map(x -> x * x).sum();
		return Math.sqrt(sqSum);
	}

	/**
	 * Generates a random set of points through which lines pass the plane.
	 * <p>
	 * If the direction of the lines is (0, 0, 1), then their coordinate range of
	 * the points will be will be (c<sub>x</sub> &plusmn; d/2, c<sub>y</sub>
	 * &plusmn; d/2, c<sub>z</sub>), where <b>c</b> is the centroid, and
	 * <em>d</em> is the length of the largest, "corner-to-corner" diagonal of the
	 * interval.
	 * </p>
	 * <p>
	 * The points are equidistant from each other in both directions, but
	 * translated randomly so that they don't always have the same co-ordinates.
	 * </p>
	 * <p>
	 * NB points may lay outside the interval!
	 * </p>
	 *
	 * @param bins number of sub-regions in both dimensions. Bins = 2 creates four
	 *          points, one from each quadrant of the plane.
	 * @return a finite stream of origin points on the plane.
	 */
	Stream<Vector3dc> getOrigins(final Long bins) {
		final Builder<Vector3dc> builder = Stream.builder();
		final double step = 1.0 / bins;
		final double uOffset = random.nextDouble() * step;
		final double tOffset = random.nextDouble() * step;
		for (long i = 0; i < bins; i++) {
			final double u = i * step + uOffset;
			for (long j = 0; j < bins; j++) {
				final double t = j * step + tOffset;
				final Vector3dc origin = createOrigin(t, u);
				builder.add(origin);
			}
		}
		return builder.build();
	}

	/**
	 * Gets a reference of the plane's normal.
	 *
	 * @return direction of the lines passing through the plane.
	 */
	Vector3dc getDirection() {
		return direction;
	}

	/**
	 * Sets the seed of the pseudo random number generator used in drawing the
	 * origin points.
	 *
	 * @see #getOrigins(Long)
	 * @param seed a seed number.
	 */
	void setSeed(final long seed) {
		random.setSeed(seed);
	}

	// endregion
}
