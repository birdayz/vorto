/*******************************************************************************
 *  Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v1.0 which accompany this distribution.
 *   
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  The Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *   
 *  Contributors:
 *  Bosch Software Innovations GmbH - Please refer to git log
 *******************************************************************************/
package org.eclipse.vorto.codegen.service.webdevice;

import org.eclipse.vorto.codegen.api.ChainedCodeGeneratorTask;
import org.eclipse.vorto.codegen.api.GenerationResultZip;
import org.eclipse.vorto.codegen.api.GeneratorTaskFromFileTemplate;
import org.eclipse.vorto.codegen.api.IGenerationResult;
import org.eclipse.vorto.codegen.api.IMappingContext;
import org.eclipse.vorto.codegen.api.IVortoCodeGenerator;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.AppScriptFileTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.ApplicationMainTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.ApplicationYmlTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.ControllersScriptFileTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.DeviceInfoServiceTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.DeviceInfoTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.IndexHtmlFileTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.PageTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.PomFileTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.ReadmeTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.ServiceClassTemplate;
import org.eclipse.vorto.codegen.service.webdevice.tasks.templates.WebSocketConfigTemplate;
import org.eclipse.vorto.core.api.model.functionblock.FunctionblockModel;
import org.eclipse.vorto.core.api.model.informationmodel.FunctionblockProperty;
import org.eclipse.vorto.core.api.model.informationmodel.InformationModel;
import org.eclipse.vorto.service.generator.CopyResourcesTask;
import org.springframework.stereotype.Service;

/**
 * @author Alexander Edelmann - Robert Bosch (SEA) Pte. Ltd.
 */
@Service
public class WebDeviceGenerator implements IVortoCodeGenerator {
			
	@Override
	public IGenerationResult generate(InformationModel context, IMappingContext mappingContext) {
		
		GenerationResultZip outputter = new GenerationResultZip(context,getServiceKey());
		for (FunctionblockProperty property : context.getProperties()) {			
			ChainedCodeGeneratorTask<FunctionblockModel> generator = new ChainedCodeGeneratorTask<FunctionblockModel>();
			generator.addTask(new GeneratorTaskFromFileTemplate<>(new ServiceClassTemplate()));
			generator.addTask(new GeneratorTaskFromFileTemplate<>(new PageTemplate()));
			generator.generate(property.getType(),mappingContext, outputter);
		}
		
		ChainedCodeGeneratorTask<InformationModel> generator = new ChainedCodeGeneratorTask<InformationModel>();
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new WebSocketConfigTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new PomFileTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new IndexHtmlFileTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new DeviceInfoTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new DeviceInfoServiceTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new ControllersScriptFileTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new AppScriptFileTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new ApplicationMainTemplate()));
		generator.addTask(new CopyResourcesTask<InformationModel>("templates","webdevice.example/src/main/resources/static"));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new ApplicationYmlTemplate()));
		generator.addTask(new GeneratorTaskFromFileTemplate<>(new ReadmeTemplate()));
		generator.generate(context, mappingContext, outputter);
		
		return outputter;
	}
		
	@Override
	public String getServiceKey() {
		return "webui";
	}
}