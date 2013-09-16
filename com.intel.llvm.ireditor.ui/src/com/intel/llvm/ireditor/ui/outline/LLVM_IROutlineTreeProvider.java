/*
Copyright (c) 2013, Intel Corporation

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the docProvider
      and/or other materials provided with the distribution.
    * Neither the name of Intel Corporation nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.intel.llvm.ireditor.ui.outline;

import java.util.HashMap;
import java.util.Map;

import com.intel.llvm.ireditor.lLVM_IR.Alias;
import com.intel.llvm.ireditor.lLVM_IR.BasicBlock;
import com.intel.llvm.ireditor.lLVM_IR.FunctionDecl;
import com.intel.llvm.ireditor.lLVM_IR.FunctionDef;
import com.intel.llvm.ireditor.lLVM_IR.GlobalVariable;
import com.intel.llvm.ireditor.lLVM_IR.MetadataNode;
import com.intel.llvm.ireditor.lLVM_IR.Model;
import com.intel.llvm.ireditor.lLVM_IR.NamedMetadata;
import com.intel.llvm.ireditor.lLVM_IR.TypeDef;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.util.ITextRegion;

/**
 * customization of the default outline structure
 * 
 */
public class LLVM_IROutlineTreeProvider extends DefaultOutlineTreeProvider {
	
	private boolean isOneOf(Object obj, Class<?>[] types) {
		for (Class<?> c : types) {
			if (c.isInstance(obj)) return true;
		}
		return false;
	}
	
	private Map<String, IOutlineNode> stringNodes = new HashMap<String, IOutlineNode>();
	
	private IOutlineNode getStringNode(final IOutlineNode parent, String s) {
		IOutlineNode result = stringNodes.get(s);
		if (result == null) {
			result = new AbstractOutlineNode(parent, parent.getImage(), "Metadata", false) {
				@Override
				public ITextRegion getSignificantTextRegion() {
					return parent.getSignificantTextRegion();
				}
				
				@Override
				public ITextRegion getFullTextRegion() {
					return parent.getFullTextRegion();
				}
				
			};
			stringNodes.put(s, result);
		}
		return result;
	}
	
	@Override
	protected void createNode(IOutlineNode parent, EObject modelElement) {
		if (isOneOf(modelElement, new Class<?>[] {
				MetadataNode.class,
				NamedMetadata.class
				})) {
			super.createNode(getStringNode(parent, "Metadata"), modelElement);
		} else if (isOneOf(modelElement, new Class<?>[] {
				Model.class,
				FunctionDef.class,
				})) {
			super.createNode(parent, modelElement);
		} else if (isOneOf(modelElement, new Class<?>[] {
				Alias.class,
				GlobalVariable.class,
				TypeDef.class,
				BasicBlock.class,
				FunctionDecl.class,
				})) {
			createEObjectNode(parent, modelElement, imageDispatcher.invoke(modelElement),
					textDispatcher.invoke(modelElement), true);
		} else if (modelElement instanceof FunctionDef) {
			FunctionDef func = (FunctionDef) modelElement;
			createEObjectNode(parent, func, imageDispatcher.invoke(func), textDispatcher.invoke(func), false);
		}
	}
	
	@Override
	public void createChildren(IOutlineNode parent, EObject modelElement) {
		if (isOneOf(modelElement, new Class<?>[]
				{Model.class, FunctionDef.class})) {
			super.createChildren(parent, modelElement);
		}
	}
}