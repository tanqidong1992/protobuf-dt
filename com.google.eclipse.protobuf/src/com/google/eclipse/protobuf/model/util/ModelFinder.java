/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Collections.unmodifiableList;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Singleton;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;

import java.util.*;

/**
 * Utility methods to find elements in a parser proto file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class ModelFinder {
  
  /**
   * Returns all the <strong>local</strong> extensions of the given message.
   * @param message the given message.
   * @return all the <strong>local</strong> extensions of the given message, or an empty collection if none is found.
   */
  public Collection<MessageExtension> localExtensionsOf(Message message) {
    return extensionsOf(message, rootOf(message));
  }
  
  public Collection<MessageExtension> extensionsOf(Message message, Protobuf root) {
    Set<MessageExtension> extensions = new HashSet<MessageExtension>();
    for (MessageExtension extension : getAllContentsOfType(root, MessageExtension.class)) {
      Message referred = messageFrom(extension);
      if (message.equals(referred)) extensions.add(extension);
    }
    return extensions;
  }
  
  /**
   * Returns the message from the given extension.
   * @param extension the given extension.
   * @return the message from the given extension, or {@code null} if the extension is not referring to a message.
   */
  public Message messageFrom(MessageExtension extension) {
    MessageLink link = extension.getMessage();
    return link == null ? null : link.getTarget();
  }

  /**
   * Returns the message type of the given field, only if the type of the given field is a message.
   * @param field the given field.
   * @return the message type of the given field or {@code null} if the type of the given field is not message.
   */
  public Message messageTypeOf(MessageField field) {
    ComplexType type = typeOf(field);
    return (type instanceof Message) ? (Message) type : null;
  }
  
  /**
   * Returns the enum type of the given field, only if the type of the given field is an enum.
   * @param field the given field.
   * @return the enum type of the given field or {@code null} if the type of the given field is not enum.
   */
  public Enum enumTypeOf(MessageField field) {
    ComplexType type = typeOf(field);
    return (type instanceof Enum) ? (Enum) type : null;
  }
  
  /**
   * Returns the type of the given field.
   * @param field the given field.
   * @return the type of the given field.
   */
  public ComplexType typeOf(MessageField field) {
    TypeLink link = field.getType();
    if (!(link instanceof ComplexTypeLink)) return null;
    return ((ComplexTypeLink) link).getTarget();
  }

  /**
   * Returns the scalar type of the given field, only if the type of the given field is a scalar.
   * @param p the given field.
   * @return the scalar type of the given field or {@code null} if the type of the given field is not a scalar.
   */
  public ScalarType scalarTypeOf(MessageField p) {
    TypeLink link = (p).getType();
    if (link instanceof ScalarTypeLink)
      return ((ScalarTypeLink) link).getTarget();
    return null;
  }

  /**
   * Returns the package of the proto file containing the given object.
   * @param o the given object.
   * @return the package of the proto file containing the given object or {@code null} if the proto file does not have a
   * package.
   */
  public Package packageOf(EObject o) {
    Protobuf root = rootOf(o);
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof Package) return (Package) e;
    }
    return null;
  }

  /**
   * Returns the root element of the proto file containing the given element.
   * @param o the given element.
   * @return the root element of the proto file containing the given element.
   */
  public Protobuf rootOf(EObject o) {
    EObject current = o;
    while (!(current instanceof Protobuf)) current = current.eContainer();
    return (Protobuf) current;
  }

  /**
   * Returns all the import definitions in the given proto.
   * @param root the given proto.
   * @return all the import definitions in the given proto.
   */
  public List<Import> importsIn(Protobuf root) {
    List<Import> imports = new ArrayList<Import>();
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof Import) imports.add((Import) e);
    }
    return unmodifiableList(imports);
  }

  /**
   * Returns all the public import definitions in the given proto.
   * @param root the given proto.
   * @return all the public import definitions in the given proto.
   */
  public List<Import> publicImportsIn(Protobuf root) {
    List<Import> imports = new ArrayList<Import>();
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof PublicImport) imports.add((Import) e);
    }
    return unmodifiableList(imports);
  }

  /**
   * Returns the root element of the given resource.
   * @param resource the given resource.
   * @return the root element of the given resource, or {@code null} if the given resource does not have a root element.
   */
  public Protobuf rootOf(Resource resource) {
    if (resource instanceof XtextResource) {
      IParseResult parseResult = ((XtextResource) resource).getParseResult();
      if (parseResult != null) {
        EObject root = parseResult.getRootASTElement();
        return (Protobuf) root;
      }
    }
    TreeIterator<Object> contents = getAllContents(resource, true);
    if (contents.hasNext()) {
      Object next = contents.next();
      if (next instanceof Protobuf) return (Protobuf) next;
    }
    return null;
  }

  public Collection<MessageField> propertiesOf(Message message) {
    List<MessageField> properties = new ArrayList<MessageField>();
    for (MessageElement e :message.getElements()) {
      if (e instanceof MessageField) properties.add((MessageField) e);
    }
    return unmodifiableList(properties);
  }
}
