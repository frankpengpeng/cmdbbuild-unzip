package org.cmdbuild.api.fluent.ws;

import static java.lang.Math.toIntExact;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.cmdbuild.api.fluent.ActiveQueryRelations;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.junit.Before;
import org.junit.Test;

public class RelationsQueryTest extends AbstractWsFluentApiTest {

    private static final CardDescriptor DESCRIPTOR_1 = descriptor("foo", 1);
    private static final CardDescriptor DESCRIPTOR_2 = descriptor("bar", 2);
    private static final CardDescriptor DESCRIPTOR_3 = descriptor("baz", 3);

    private ActiveQueryRelations query;

    @Before
    public void createQuery() throws Exception {
        query = api().queryRelations(CLASS_NAME, CARD_ID) //
                .withDomain(DOMAIN_NAME);

        when(proxy().getRelationList( //
                (query.getDomainName()), //
                (query.getClassName()), //
                (toIntExact(query.getCardId())) //
        ) //
        ).thenReturn(relationList(query.getDomainName(), //
                DESCRIPTOR_1, //
                DESCRIPTOR_2, //
                DESCRIPTOR_3));
    }

    @Test
    public void parametersPassedToProxyWhenFetchingClass() throws Exception {
        query.fetch();

        verify(proxy()).getRelationList( //
                (query.getDomainName()), //
                (query.getClassName()), //
                (toIntExact(query.getCardId())));
        verifyNoMoreInteractions(proxy());
    }

    @Test
    public void soapRelationsAreConvertedToFluentApiCardDescriptorsWhenFetchingRelations() throws Exception {
        final List<CardDescriptor> descriptors = query.fetch();
        assertThat(descriptors, hasItem(DESCRIPTOR_1));
        assertThat(descriptors, hasItem(DESCRIPTOR_2));
        assertThat(descriptors, hasItem(DESCRIPTOR_3));
    }

    private static List<org.cmdbuild.services.soap.client.beans.Relation> relationList(final String domainName,
            final CardDescriptor... descriptors) {
        final List<org.cmdbuild.services.soap.client.beans.Relation> relations = new ArrayList<>();
        for (final CardDescriptor descriptor : descriptors) {
            final org.cmdbuild.services.soap.client.beans.Relation relation = new org.cmdbuild.services.soap.client.beans.Relation();
            relation.setDomainName(domainName);
            relation.setClass1Name(CLASS_NAME);
            relation.setCard1Id(CARD_ID);
            relation.setClass2Name(descriptor.getClassName());
            relation.setCard2Id(descriptor.getId());
            relations.add(relation);
        }
        return relations;
    }

    private static CardDescriptor descriptor(final String className, final long id) {
        return new CardDescriptorImpl(className, id);
    }

}
