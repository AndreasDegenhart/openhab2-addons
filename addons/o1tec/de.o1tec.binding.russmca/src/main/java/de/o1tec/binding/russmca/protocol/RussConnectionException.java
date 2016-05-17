/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol;

/**
 * Exception for Russound errors.
 *
 * @author Andreas Degenhart
 */
public class RussConnectionException extends RuntimeException {

    private static final long serialVersionUID = -7970958467980752003L;

    public RussConnectionException() {
        super();
    }

    public RussConnectionException(String message) {
        super(message);
    }

    public RussConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RussConnectionException(Throwable cause) {
        super(cause);
    }

}
