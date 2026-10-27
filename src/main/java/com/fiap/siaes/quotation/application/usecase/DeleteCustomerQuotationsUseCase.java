package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;

public interface DeleteCustomerQuotationsUseCase {

    void execute(DeleteCustomerQuotationsCommand command);

    record DeleteCustomerQuotationsCommand(CustomerId customerId, UserId deletedBy) {

        public static DeleteCustomerQuotationsCommand of(CustomerId customerId, UserId deletedBy) {
            return new DeleteCustomerQuotationsCommand(customerId, deletedBy);
        }
    }
}
