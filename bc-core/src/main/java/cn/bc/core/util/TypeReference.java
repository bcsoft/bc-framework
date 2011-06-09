package cn.bc.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * 获取参数类型的抽象类
 * @author dragon
 *
 * @param <T>
 */
public abstract class TypeReference<T> {
	private final Type _type;
	private final Class<T> rawType;

	protected TypeReference() {
		Type superClass = getClass().getGenericSuperclass();
		if (superClass instanceof Class<?>) { // sanity check, should never
			// happen
			throw new IllegalArgumentException(
					"Internal error: TypeReference constructed without actual type information");
		}
		/*
		 * 22-Dec-2008, tatu: Not sure if this case is safe -- I suspect it is
		 * possible to make it fail? But let's deal with specifc case when we
		 * know an actual use case, and thereby suitable work arounds for valid
		 * case(s) and/or error to throw on invalid one(s).
		 */
		_type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

		this.rawType = (Class<T>) getRawType(_type);
	}

	@SuppressWarnings("unchecked")
	private Class<T> getRawType(Type type) {
		if (type instanceof Class) {
			return (Class<T>) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType actualType = (ParameterizedType) type;
			return getRawType(actualType.getRawType());
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Object rawArrayType = Array.newInstance(getRawType(genericArrayType
					.getGenericComponentType()), 0);
			return (Class<T>) rawArrayType.getClass();
		} else if (type instanceof WildcardType) {
			WildcardType castedType = (WildcardType) type;
			return getRawType(castedType.getUpperBounds()[0]);
		} else {
			throw new IllegalArgumentException(
					"Type \'"
							+ type
							+ "\' is not a Class, "
							+ "ParameterizedType, or GenericArrayType. Can't extract class.");
		}
	}

	public Type getType() {
		return _type;
	}

	public Class<T> getRawType() {
		return rawType;
	}
}
