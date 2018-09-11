package org.bonej.ops.ellipsoid;

import net.imagej.ImageJ;
import net.imagej.ops.special.function.BinaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imglib2.util.ValuePair;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *Tests for {@link FindEllipsoidFromBoundaryPoints}
 *lots of expected values calculated with pen and paper or sympy.
 *TODO: put calculations in javadoc and user documentation
 */

public class FindEllipsoidFromBoundaryPointsTest {
    private static final ImageJ IMAGE_J = new ImageJ();

    @SuppressWarnings("unchecked")
    private static BinaryFunctionOp<List<ValuePair<Vector3d, Vector3d>>, Vector3d, Optional<Ellipsoid>> findEllipsoidOp =
            (BinaryFunctionOp) Functions.binary(IMAGE_J.op(), FindEllipsoidFromBoundaryPoints.class,
                    Optional.class, List.class, Vector3d.class);

    @Test
    public void testFittingEllipsoidToEquidistantCollisionPoints() {
        Vector3d vertexP = new Vector3d(-2, 0, 0);
        Vector3d normalP = new Vector3d(1, 0, 0);
        ValuePair<Vector3d,Vector3d> p = new ValuePair<>(vertexP,normalP);

        Vector3d vertexQ = new Vector3d(2, 0, 0);
        Vector3d normalQ = new Vector3d(-1, 0, 0);
        ValuePair<Vector3d,Vector3d> q = new ValuePair<>(vertexQ,normalQ);

        Vector3d vertexR = new Vector3d(0, 2, 0);
        Vector3d normalR = new Vector3d(0, -1, 0);
        ValuePair<Vector3d,Vector3d> r = new ValuePair<>(vertexR,normalR);

        Vector3d vertexS = new Vector3d(0, -2, 0);
        Vector3d normalS = new Vector3d(0, 1, 0);
        ValuePair<Vector3d,Vector3d> s = new ValuePair<>(vertexS,normalS);

        Vector3d vertexTooFarAway = new Vector3d(10, -20, 4);

        final List<ValuePair<Vector3d, Vector3d>> fourVertices = Arrays.asList(p, q, r, s);

        Optional<Ellipsoid> ellipsoid = findEllipsoidOp.calculate(fourVertices,new Vector3d(0,0,0));

        assertTrue(ellipsoid.isPresent());
        assertTrue(testPointIsOnEllipsoidSurface(vertexP, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexQ, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexR, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexS, ellipsoid.get()));
        assertTrue(!testPointIsOnEllipsoidSurface(vertexTooFarAway, ellipsoid.get()));
    }

    @Test
    public void testFittingEllipsoidToFiveInputPointsEasy() {
        Vector3d vertexP = new Vector3d(0, 2, 0);
        Vector3d normalP = new Vector3d(0, 1, 0);
        ValuePair<Vector3d,Vector3d> p = new ValuePair<>(vertexP,normalP);

        Vector3d vertexQ = new Vector3d(0, 4, 0);
        Vector3d normalQ = new Vector3d(-1, 0, 0);
        ValuePair<Vector3d,Vector3d> q = new ValuePair<>(vertexQ,normalQ);

        Vector3d vertexR = new Vector3d(3, 3, 0);
        Vector3d normalR = new Vector3d(0, -1, 0);
        ValuePair<Vector3d,Vector3d> r = new ValuePair<>(vertexR,normalR);

        Vector3d vertexS = new Vector3d(-3, 3, 0);
        Vector3d normalS = new Vector3d(0, 1, 0);
        ValuePair<Vector3d,Vector3d> s = new ValuePair<>(vertexS,normalS);

        Vector3d vertexTooFarAway = new Vector3d(10, -20, 4);

        final List<ValuePair<Vector3d, Vector3d>> fourVertices = Arrays.asList(p, q, r, s);

        Optional<Ellipsoid> ellipsoid = findEllipsoidOp.calculate(fourVertices, new Vector3d(0,3,0));

        assertTrue(ellipsoid.isPresent());
        assertTrue(testPointIsOnEllipsoidSurface(vertexP, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexQ, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexR, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexS, ellipsoid.get()));
        assertTrue(!testPointIsOnEllipsoidSurface(vertexTooFarAway, ellipsoid.get()));
    }

    @Test
    public void testFittingEllipsoidToFiveInputPointsDifficult() {
        Vector3d vertexP = new Vector3d(0, 0, 0);
        Vector3d normalP = new Vector3d(0, 1, 0);
        ValuePair<Vector3d,Vector3d> p = new ValuePair<>(vertexP,normalP);

        Vector3d vertexQ = new Vector3d(1, 3, 0);
        Vector3d normalQ = new Vector3d(-1, 0, 0);
        ValuePair<Vector3d,Vector3d> q = new ValuePair<>(vertexQ,normalQ);

        Vector3d vertexR = new Vector3d(-4, 2, 0);
        Vector3d normalR = new Vector3d(0, -1, 0);
        ValuePair<Vector3d,Vector3d> r = new ValuePair<>(vertexR,normalR);

        Vector3d vertexS = new Vector3d(-1.5, 3, 8);
        Vector3d normalS = new Vector3d(0, 0, -1);
        ValuePair<Vector3d,Vector3d> s = new ValuePair<>(vertexS,normalS);

        Vector3d vertexTooFarAway = new Vector3d(10, -20, 4);

        final List<ValuePair<Vector3d, Vector3d>> fourVertices = Arrays.asList(p, q, r, s);

        Optional<Ellipsoid> ellipsoid = findEllipsoidOp.calculate(fourVertices, new Vector3d(-1,2,0));

        assertTrue(ellipsoid.isPresent());
        assertTrue(testPointIsOnEllipsoidSurface(vertexP, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexQ, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexR, ellipsoid.get()));
        assertTrue(testPointIsOnEllipsoidSurface(vertexS, ellipsoid.get()));
        assertTrue(!testPointIsOnEllipsoidSurface(vertexTooFarAway, ellipsoid.get()));
    }

    private boolean testPointIsOnEllipsoidSurface(Vector3d point, Ellipsoid ellipsoid) {
        Vector3d xminusC = ellipsoid.getCentroid();
        xminusC.mul(-1);
        xminusC.add(point);

        Matrix3d rotationFromAxisAligned = new Matrix3d();
        ellipsoid.getOrientation().get3x3(rotationFromAxisAligned);

        Matrix3d rotationToAxisAligned = new Matrix3d(rotationFromAxisAligned);
        rotationToAxisAligned.transpose();

        List<Vector3d> ellipsoidSemiAxes = ellipsoid.getSemiAxes();
        Matrix3d scale = new Matrix3d();
        scale.m00 = 1.0 / (ellipsoidSemiAxes.get(0).lengthSquared());
        scale.m11 = 1.0 / (ellipsoidSemiAxes.get(1).lengthSquared());
        scale.m22 = 1.0 / (ellipsoidSemiAxes.get(2).lengthSquared());

        Matrix3d SR = new Matrix3d(scale);
        SR.mul(rotationToAxisAligned);
        Matrix3d A = new Matrix3d();
        rotationFromAxisAligned.mul(SR, A);

        Vector3d Ax = new Vector3d(xminusC);
        A.transform(Ax);

        double shouldBeOne = xminusC.dot(Ax);

        return Math.abs(shouldBeOne - 1.0) < 1.0e-12;
    }

    @Test
    public void testQ1() {
        Vector3d sphereCentre = new Vector3d(3, 4, 5);
        double radius = 7.77;

        Matrix4d q1 = FindEllipsoidFromBoundaryPoints.getQ1(sphereCentre, radius);

        Matrix3d identity = new Matrix3d();
        identity = identity.identity();
        Matrix3d q1Rotation = new Matrix3d();
        q1.get3x3(q1Rotation);
        assertEquals(identity, q1Rotation);

        Vector4d expected = new Vector4d(-3, -4, -5, 50 - 7.77 * 7.77);

        Vector4d bottomRow = new Vector4d();
        q1.getRow(3, bottomRow);
        assertEquals(expected, bottomRow);

        Vector4d rightColumn = new Vector4d();
        q1.getColumn(3, rightColumn);
        assertEquals(expected, rightColumn);

    }

    @Test
    public void testQ2() {
        Vector3d p = new Vector3d(4, 4, 1);
        Vector3d q = new Vector3d(2, 2, 1);
        Vector3d np = new Vector3d(-Math.sqrt(2) / 2.0, -Math.sqrt(2) / 2.0, 0);
        Vector3d nq = new Vector3d(Math.sqrt(2) / 2.0, Math.sqrt(2) / 2.0, 0);


        // @formatter:off
        final Matrix4d expected = new Matrix4d(
                0.5, 0.5, 0.0, -3.0,
                0.5, 0.5, 0.0, -3.0,
                0.0, 0.0, 0.0, 0.0,
                -3.0, -3.0, 0.0, 16.0
        );
        // @formatter:on

        Matrix4d q2 = FindEllipsoidFromBoundaryPoints.getQ2(new VertexWithNormal(new ValuePair<>(p, np)), new VertexWithNormal(new ValuePair<>(q, nq)));

        assertEquals(q2.m00(),expected.m00(),1.0e-12);
        assertEquals(q2.m01(),expected.m01(),1.0e-12);
        assertEquals(q2.m02(),expected.m02(),1.0e-12);
        assertEquals(q2.m03(),expected.m03(),1.0e-12);

        assertEquals(q2.m10(),expected.m10(),1.0e-12);
        assertEquals(q2.m11(),expected.m11(),1.0e-12);
        assertEquals(q2.m12(),expected.m12(),1.0e-12);
        assertEquals(q2.m13(),expected.m13(),1.0e-12);

        assertEquals(q2.m20(),expected.m20(),1.0e-12);
        assertEquals(q2.m21(),expected.m21(),1.0e-12);
        assertEquals(q2.m22(),expected.m22(),1.0e-12);
        assertEquals(q2.m23(),expected.m23(),1.0e-12);

        assertEquals(q2.m30(),expected.m30(),1.0e-12);
        assertEquals(q2.m31(),expected.m31(),1.0e-12);
        assertEquals(q2.m32(),expected.m32(),1.0e-12);
        assertEquals(q2.m33(),expected.m33(),1.0e-12);
    }
}
