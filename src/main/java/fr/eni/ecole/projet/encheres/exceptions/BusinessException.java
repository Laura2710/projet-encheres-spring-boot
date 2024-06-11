package fr.eni.ecole.projet.encheres.exceptions;

import java.util.ArrayList;
import java.util.List;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private List<String> clefsExternalisations;

	public BusinessException() {
		super();
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public List<String> getClefsExternalisations() {
		return clefsExternalisations;
	}

	public void add(String clef) {
		if (clefsExternalisations == null) {
			clefsExternalisations = new ArrayList<String>();
		}
		clefsExternalisations.add(clef);
	}

	public boolean isValid() {
		return clefsExternalisations == null || clefsExternalisations.isEmpty();
	}
}
