package com.hbm.animloader;

import com.hbm.util.Vec3dUtil;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

public class Transform {

	protected static FloatBuffer auxGLMatrix = GLAllocation.createDirectFloatBuffer(16);
	
	Vec3d scale;
	Vec3d translation;
	Quaternion rotation;
	
	boolean hidden = false;
	
	public Transform(float[] matrix){
		scale = getScaleFromMatrix(matrix);
		auxGLMatrix.put(matrix);
		auxGLMatrix.rewind();
		rotation = new Quaternion().setFromMatrix((Matrix4f) new Matrix4f().load(auxGLMatrix));
		translation = new Vec3d(matrix[0*4+3], matrix[1*4+3], matrix[2*4+3]);
		auxGLMatrix.rewind();
	}
	
	private Vec3d getScaleFromMatrix(float[] matrix){
		float scaleX = (float) new Vec3d(matrix[0], matrix[1], matrix[2]).length();
		float scaleY = (float) new Vec3d(matrix[4], matrix[5], matrix[6]).length();
		float scaleZ = (float) new Vec3d(matrix[8], matrix[9], matrix[10]).length();
		
		matrix[0] = matrix[0]/scaleX;
		matrix[1] = matrix[1]/scaleX;
		matrix[2] = matrix[2]/scaleX;
		
		matrix[4] = matrix[4]/scaleY;
		matrix[5] = matrix[5]/scaleY;
		matrix[6] = matrix[6]/scaleY;
		
		matrix[8] = matrix[8]/scaleZ;
		matrix[9] = matrix[9]/scaleZ;
		matrix[10] = matrix[10]/scaleZ;
		return new Vec3d(scaleX, scaleY, scaleZ);
	}
	
	public void interpolateAndApply(Transform other, float inter){
		Vec3d trans = Vec3dUtil.lerp(translation, other.translation, inter);
		Vec3d scale = Vec3dUtil.lerp(this.scale, other.scale, inter);
		Quaternion rot = slerp(rotation, other.rotation, inter);
		GlStateManager.quatToGlMatrix(auxGLMatrix, rot);
		scale(auxGLMatrix, scale);
		auxGLMatrix.put(12, (float) trans.x);
		auxGLMatrix.put(13, (float) trans.y);
		auxGLMatrix.put(14, (float) trans.z);
		
		//for(int i = 0; i < 16; i ++){
			//System.out.print(auxGLMatrix.get(i) + " ");
		//}
		//System.out.println();
		GlStateManager.multMatrix(auxGLMatrix);
	}
	
	private void scale(FloatBuffer matrix, Vec3d scale){
		matrix.put(0, (float) (matrix.get(0)*scale.x));
		matrix.put(4, (float) (matrix.get(4)*scale.x));
		matrix.put(8, (float) (matrix.get(8)*scale.x));
		matrix.put(12, (float) (matrix.get(12)*scale.x));
		
		matrix.put(1, (float) (matrix.get(1)*scale.y));
		matrix.put(5, (float) (matrix.get(5)*scale.y));
		matrix.put(9, (float) (matrix.get(9)*scale.y));
		matrix.put(13, (float) (matrix.get(13)*scale.y));
		
		matrix.put(2, (float) (matrix.get(2)*scale.z));
		matrix.put(6, (float) (matrix.get(6)*scale.z));
		matrix.put(10, (float) (matrix.get(10)*scale.z));
		matrix.put(14, (float) (matrix.get(14)*scale.z));
	}
	
	//Thanks, wikipedia
	//God, I wish java had operator overloads. Those are one of my favorite things about c and glsl.
	protected Quaternion slerp(Quaternion v0, Quaternion v1, float t) {
		// Only unit quaternions are valid rotations.
	    // Normalize to avoid undefined behavior.
		//Drillgon200: Any quaternions loaded from blender should be normalized already
	    //v0.normalise();
	    //v1.normalise();

	    // Compute the cosine of the angle between the two vectors.
	    double dot = Quaternion.dot(v0, v1);

	    // If the dot product is negative, slerp won't take
	    // the shorter path. Note that v1 and -v1 are equivalent when
	    // the negation is applied to all four components. Fix by 
	    // reversing one quaternion.
	    if (dot < 0.0f) {
	        v1 = new Quaternion(-v1.x, -v1.y, -v1.z, -v1.w);
	        dot = -dot;
	    }

	    final double DOT_THRESHOLD = 0.9999999;
	    if (dot > DOT_THRESHOLD) {
	        // If the inputs are too close for comfort, linearly interpolate
	        // and normalize the result.
	        Quaternion result = new Quaternion(v0.x + t*v1.x, 
	        								v0.y + t*v1.y, 
	        								v0.z + t*v1.z, 
	        								v0.w + t*v1.w);
	        result.normalise();
	        return result;
	    }

	    // Since dot is in range [0, DOT_THRESHOLD], acos is safe
	    double theta_0 = Math.acos(dot);        // theta_0 = angle between input vectors
	    double theta = theta_0*t;          // theta = angle between v0 and result
	    double sin_theta = Math.sin(theta);     // compute this value only once
	    double sin_theta_0 = Math.sin(theta_0); // compute this value only once

	    float s0 = (float) (Math.cos(theta) - dot * sin_theta / sin_theta_0);  // == sin(theta_0 - theta) / sin(theta_0)
	    float s1 = (float) (sin_theta / sin_theta_0);

	    return new Quaternion(s0*v0.x + s1*v1.x, 
	    					s0*v0.y + s1*v1.y, 
	    					s0*v0.z + s1*v1.z, 
	    					s0*v0.w + s1*v1.w);
	}
	
}
