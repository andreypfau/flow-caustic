/*
 * This file is part of Caustic.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic is licensed under the Spout License Version 1.
 *
 * Caustic is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.renderer;

import java.awt.Color;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.renderer.Shader.ShaderType;
import org.spout.renderer.data.UniformHolder;

/**
 * Represents a shader program for OpenGL. A program holds the necessary shaders for the rendering
 * pipeline. The GL20 and GL30 versions require at least that sources for the {@link
 * ShaderType#VERTEX} and {@link ShaderType#FRAGMENT} shaders be set before creation. When using
 * GL20, it is strongly recommended to set the attribute layout in the program, which must be done
 * before creation. The layout allows for association between the attribute index in the vertex data
 * and the name in the shaders. For GL30, it is recommended to do so in the shaders instead, using
 * the "layout" keyword. Failing to do so might result in partial, wrong or missing rendering, and
 * affects models using multiple attributes. The texture layout should also be setup. This one can
 * be done after creation, but is necessary for assigning texture units to sampler uniforms.
 */
public abstract class Program extends Creatable {
	protected int id;
	// Shader sources
	protected Map<ShaderType, InputStream> shaderSources;
	// Map of the attribute names to their vao index (optional for GL30 as they can be defined in the shader instead)
	protected TObjectIntMap<String> attributeLayouts;
	// Map of the texture units to their names. Only necessary if textures are used
	protected TIntObjectMap<String> textureLayouts;

	@Override
	public void create() {
		shaderSources = null;
		attributeLayouts = null;
		super.create();
	}

	@Override
	public void destroy() {
		textureLayouts = null;
		id = 0;
		super.destroy();
	}

	/**
	 * Binds this program to the OpenGL context.
	 */
	public abstract void bind();

	/**
	 * Unbinds this program from the OpenGL context.
	 */
	public abstract void unbind();

	/**
	 * Binds the texture unit to the shader uniform. The binding is done according to the texture
	 * layout, which must be set in the program for the textures that will be used before any binding
	 * can be done.
	 *
	 * @param unit The unit to bind
	 */
	public abstract void bindTexture(int unit);

	/**
	 * Uploads the uniforms to this program.
	 *
	 * @param uniforms The uniforms to upload
	 */
	public abstract void upload(UniformHolder uniforms);

	/**
	 * Sets a uniform boolean in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param b The boolean value
	 */
	public abstract void setUniform(String name, boolean b);

	/**
	 * Sets a uniform integer in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param i The integer value
	 */
	public abstract void setUniform(String name, int i);

	/**
	 * Sets a uniform float in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param f The float value
	 */
	public abstract void setUniform(String name, float f);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public abstract void setUniform(String name, Vector2 v);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public abstract void setUniform(String name, Vector3 v);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public abstract void setUniform(String name, Vector4 v);

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public abstract void setUniform(String name, Matrix2 m);

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public abstract void setUniform(String name, Matrix3 m);

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public abstract void setUniform(String name, Matrix4 m);

	/**
	 * Sets a uniform {@link java.awt.Color} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param c The color value
	 */
	public abstract void setUniform(String name, Color c);

	/**
	 * Gets the ID for this program as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the source of the shader for the type.
	 *
	 * @param type The target type
	 * @param source The source to set
	 */
	public void addShaderSource(ShaderType type, InputStream source) {
		if (shaderSources == null) {
			shaderSources = new EnumMap<>(ShaderType.class);
		}
		shaderSources.put(type, source);
	}

	/**
	 * Returns true if a shader source is present for the type.
	 *
	 * @param type The type to check
	 * @return Whether or not a shader source is present
	 */
	public boolean hasShaderSource(ShaderType type) {
		return shaderSources != null && shaderSources.containsKey(type);
	}

	/**
	 * Returns the shader source for the type, or null if none has been set.
	 *
	 * @param type The to lookup
	 * @return The shader source
	 */
	public InputStream getShaderSource(ShaderType type) {
		return shaderSources != null ? shaderSources.get(type) : null;
	}

	/**
	 * Removes the shader source associated to the type, if present.
	 *
	 * @param type The type to remove
	 */
	public void removeShaderSource(ShaderType type) {
		if (shaderSources != null) {
			shaderSources.remove(type);
			if (shaderSources.isEmpty()) {
				shaderSources = null;
			}
		}
	}

	/**
	 * Sets the index of the attribute of the provided name, in the program.
	 *
	 * @param name The name of the attribute
	 * @param index The index for the attribute
	 */
	public void addAttributeLayout(String name, int index) {
		if (attributeLayouts == null) {
			attributeLayouts = new TObjectIntHashMap<>();
		}
		attributeLayouts.put(name, index);
	}

	/**
	 * Returns true if an attribute of the provided name has been set to an index.
	 *
	 * @param name The name to lookup
	 * @return Whether or not the layout is set for the attribute
	 */
	public boolean hasAttributeLayout(String name) {
		return attributeLayouts != null && attributeLayouts.containsKey(name);
	}

	/**
	 * Returns the index for the attribute of the provided name.
	 *
	 * @param name The name to lookup
	 * @return The index
	 */
	public int getAttributeLayout(String name) {
		return attributeLayouts != null ? attributeLayouts.get(name) : -1;
	}

	/**
	 * Removes the index for the attribute of the provided name.
	 */
	public void removeAttributeLayout(String name) {
		if (attributeLayouts != null) {
			attributeLayouts.remove(name);
			if (attributeLayouts.isEmpty()) {
				attributeLayouts = null;
			}
		}
	}

	/**
	 * Sets the unit of the texture to the provided name, in the program.
	 *
	 * @param name The name of the texture
	 * @param unit The unit for the texture
	 */
	public void addTextureLayout(String name, int unit) {
		if (textureLayouts == null) {
			textureLayouts = new TIntObjectHashMap<>();
		}
		textureLayouts.put(unit, name);
	}

	/**
	 * Returns true if a texture at the provided unit has been set to a name.
	 *
	 * @param unit The unit to lookup
	 * @return Whether or not the layout is set for the texture
	 */
	public boolean hasTextureLayout(int unit) {
		return textureLayouts != null && textureLayouts.containsKey(unit);
	}

	/**
	 * Returns the name for the texture at the provided unit.
	 *
	 * @param unit The unit to lookup
	 * @return The name
	 */
	public String getTextureLayout(int unit) {
		return textureLayouts != null ? textureLayouts.get(unit) : null;
	}

	/**
	 * Removes the layout for the texture at the provided unit.
	 */
	public void removeTextureLayout(int unit) {
		if (textureLayouts != null) {
			textureLayouts.remove(unit);
			if (textureLayouts.isEmpty()) {
				textureLayouts = null;
			}
		}
	}
}
