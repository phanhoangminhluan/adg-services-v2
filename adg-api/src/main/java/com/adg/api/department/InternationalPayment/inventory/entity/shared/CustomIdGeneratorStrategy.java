package com.adg.api.department.InternationalPayment.inventory.entity.shared;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

import static java.util.Objects.isNull;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:14
 */
public class CustomIdGeneratorStrategy extends UUIDGenerator {

    @SneakyThrows
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Serializable id = session.getEntityPersister(null, object)
                .getClassMetadata().getIdentifier(object, session);

        return isNull(id) ? super.generate(session, object) : id;
    }
}

