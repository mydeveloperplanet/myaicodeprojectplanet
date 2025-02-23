package com.mydeveloperplanet.myaicodeprojectplanet.repository;

import com.mydeveloperplanet.myaicodeprojectplanet.jooq.tables.Customers;
import com.mydeveloperplanet.myaicodeprojectplanet.jooq.tables.records.CustomersRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    @Autowired
    private DSLContext dslContext;

    public List<CustomersRecord> getAllCustomers() {
        return dslContext.selectFrom(Customers.CUSTOMERS).fetchInto(CustomersRecord.class);
    }

    public Optional<CustomersRecord> getCustomerById(Long id) {
        return dslContext.selectFrom(Customers.CUSTOMERS)
                .where(Customers.CUSTOMERS.ID.eq(id))
                .fetchOptionalInto(CustomersRecord.class);
    }

    public CustomersRecord createCustomer(CustomersRecord customer) {
        dslContext.insertInto(Customers.CUSTOMERS, 
                             Customers.CUSTOMERS.FIRST_NAME, 
                             Customers.CUSTOMERS.LAST_NAME)
                 .values(customer.getFirstName(), customer.getLastName())
                 .returning()
                 .fetchOne();
        return customer;
    }

    public CustomersRecord updateCustomer(Long id, CustomersRecord customerDetails) {
        boolean exists = dslContext.fetchExists(dslContext.selectFrom(Customers.CUSTOMERS));
        if (exists) {
            dslContext.update(Customers.CUSTOMERS)
                    .set(Customers.CUSTOMERS.FIRST_NAME, customerDetails.getFirstName())
                    .set(Customers.CUSTOMERS.LAST_NAME, customerDetails.getLastName())
                    .where(Customers.CUSTOMERS.ID.eq(id))
                    .returning()
                    .fetchOne();
            return customerDetails;
        } else {
            throw new RuntimeException("Customer not found");
        }
    }

    public void deleteCustomer(Long id) {
        boolean exists = dslContext.fetchExists(dslContext.selectFrom(Customers.CUSTOMERS));
        if (exists) {
            dslContext.deleteFrom(Customers.CUSTOMERS)
                    .where(Customers.CUSTOMERS.ID.eq(id))
                    .execute();
        } else {
            throw new RuntimeException("Customer not found");
        }
    }
}
