package doldol_server.doldol.common.dto;

import java.util.List;
import java.util.function.ToLongFunction;

import lombok.Getter;

@Getter
public class CursorPage<T> {
	private final List<T> data;
	private final int size;
	private final Long nextCursor;
	private final boolean hasNext;

	private CursorPage(List<T> data, int requestedSize, ToLongFunction<T> cursorGetter, boolean hasNext) {
		this.size = hasNext ? requestedSize : data.size();
		this.hasNext = hasNext;
		this.data = data.subList(0, this.size);
		this.nextCursor = hasNext ? cursorGetter.applyAsLong(data.get(this.size)) : null;
	}

	private CursorPage(List<T> data) {
		this.data = data;
		this.size = 0;
		this.hasNext = false;
		this.nextCursor = null;
	}

	public static <T> CursorPage<T> of(List<T> data, int requestedSize, ToLongFunction<T> cursorGetter) {
		if (data.isEmpty())
			return new CursorPage<>(data);
		if (data.size() == requestedSize + 1)
			return new CursorPage<>(data, requestedSize, cursorGetter, true);
		return new CursorPage<>(data, requestedSize, cursorGetter, false);
	}

	public boolean isEmpty() {
		return this.data.isEmpty();
	}

}
