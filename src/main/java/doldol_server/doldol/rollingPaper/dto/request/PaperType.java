package doldol_server.doldol.rollingPaper.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaperType {

	GROUP("그룹용"),
	INDIVIDUAL("개인용")
	;

	private final String displayName;
}
