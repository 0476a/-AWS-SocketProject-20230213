package clientDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestDto<T> {
	private String resource;
	private String body;
}
