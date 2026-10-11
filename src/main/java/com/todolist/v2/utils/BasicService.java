package com.todolist.v2.utils;

import java.util.Objects;
import java.util.function.Function;

public interface BasicService<T, R, S> {
	
	//Request -> Entity -> Persist -> Response

	default T fromRequest(R req, Function<R, T> method) {
		Objects.requireNonNull(req);

		return Objects.requireNonNull(method).apply(req);
	}

	default S toResponse(T entity, Function<T, S> method) {
		Objects.requireNonNull(entity);

		return Objects.requireNonNull(method).apply(entity);
	}

	default <W> R anyToRequest(W obj, Function<W, R> method) {
		Objects.requireNonNull(obj);

		return Objects.requireNonNull(method).apply(obj);
	}

}
