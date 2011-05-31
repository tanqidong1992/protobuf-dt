/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {

  public static String expectedFieldName;
  public static String expectedFieldNumber;
  public static String expectedSyntaxIdentifier;
  public static String fieldNumberAlreadyUsed;
  public static String fieldNumbersMustBePositive;
  public static String importNotFound;
  public static String missingFieldNumber;
  public static String unrecognizedSyntaxIdentifier;

  static {
    Class<Messages> targetType = Messages.class;
    NLS.initializeMessages(targetType.getName(), targetType);
  }
  private Messages() {}
}
