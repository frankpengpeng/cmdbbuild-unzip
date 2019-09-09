/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.extcomponents.contextmenu;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;
import org.cmdbuild.extcomponents.commons.ExtComponentInfoImpl;
import static org.cmdbuild.extcomponents.commons.ExtComponentUtils.getCodeFromExtComponentData;
import static org.cmdbuild.extcomponents.commons.ExtComponentUtils.getComponentFile;
import static org.cmdbuild.extcomponents.commons.ExtComponentUtils.parseExtComponentData;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import org.springframework.stereotype.Component;

@Component
public class ContextMenuComponentServiceImpl implements ContextMenuComponentService {

    private final ContextMenuComponentRepository repository;

    public ContextMenuComponentServiceImpl(ContextMenuComponentRepository repository) {
        this.repository = checkNotNull(repository);
    }

    @Override
    public List<ExtComponentInfo> getForCurrentUser() {
        return repository.getAll().stream().map(this::toComponentInfo).collect(toList());//TODO filter for user
    }

    @Override
    public List<ExtComponentInfo> getActiveForCurrentUser() {
        return getForCurrentUser().stream().filter(ExtComponentInfo::getActive).collect(toList());
    }

    @Override
    public ExtComponentInfo get(Long id) {
        return toComponentInfo(repository.getById(id));
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public ExtComponentInfo createOrUpdate(byte[] data) {
        String name = getCodeFromExtComponentData(data);
        ContextMenuComponentData componentData = repository.getByNameOrNull(name);
        if (componentData == null) {
            return create(data);
        } else {
            return update(componentData.getId(), data);
        }
    }

    @Override
    public ExtComponentInfo create(byte[] data) {
        ContextMenuComponentData componentData = repository.create(ContextMenuComponentDataImpl.builder().withData(data).withName(getCodeFromExtComponentData(data)).build());
        return toComponentInfo(componentData);
    }

    @Override
    public ExtComponentInfo update(Long id, byte[] data) {
        ContextMenuComponentData componentData = repository.getById(id);
        checkArgument(equal(componentData.getName(), getCodeFromExtComponentData(data)), "invalid component code mismatch");
        componentData = repository.update(ContextMenuComponentDataImpl.copyOf(componentData).withData(data).build());
        return toComponentInfo(componentData);
    }

    @Override
    public ExtComponentInfo update(ExtComponentInfo component) {
        ContextMenuComponentData data = repository.getById(component.getId());
        data = repository.update(ContextMenuComponentDataImpl.copyOf(data).withActive(component.getActive()).withDescription(component.getDescription()).build());
        return toComponentInfo(data);
    }

    @Override
    public byte[] getContextMenuFile(String name, String filePath) {
        ContextMenuComponentData componentData = repository.getByName(name);
        return getComponentFile(componentData, componentData.getData(), filePath, true);//TODO js compression config
    }

    @Override
    public DataHandler getContextMenuData(String code) {
        ContextMenuComponentData componentData = repository.getByName(code);
        return newDataHandler(componentData.getData(), "application/zip", format("%s.zip", normalize(componentData.getName())));
    }

    private ExtComponentInfo toComponentInfo(ContextMenuComponentData data) {
        return ExtComponentInfoImpl.builder()
                .accept(parseExtComponentData(data.getData()))
                .withId(data.getId())
                .withActive(data.getActive())
                .withDescription(data.getDescription())
                .build();
    }

}
