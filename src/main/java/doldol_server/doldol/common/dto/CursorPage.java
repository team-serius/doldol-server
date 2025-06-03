package doldol_server.doldol.common.dto;

import java.util.List;
import java.util.function.Function;

import lombok.Getter;

@Getter
public class CursorPage<T, C> {
	private final List<T> data;
	private final int size;
	private final C nextCursor;
	private final boolean hasNext;

	private CursorPage(List<T> data, int requestedSize, Function<T, C> cursorGetter, boolean hasNext) {
		if (hasNext) {
			this.size = requestedSize;
			this.nextCursor = cursorGetter.apply(data.get(this.size));
		} else {
			this.size = data.size();
			this.nextCursor = null;
		}
		this.hasNext = hasNext;
		this.data = data.subList(0, this.size);
	}

	private CursorPage(List<T> data) {
		this.data = data;
		this.size = 0;
		this.hasNext = false;
		this.nextCursor = null;
	}

	public static <T, C> CursorPage<T, C> of(List<T> data, int requestedSize, Function<T, C> cursorGetter) {
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
