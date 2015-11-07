package org.robovm.store.payments;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class PaymentAPITest {

    @Test
    public void testCreatePaymentVisa() throws PayPalRESTException {
        CreditCard creditCard = new CreditCard()
                .setType("visa")
                .setNumber("4446283280247004")
                .setExpireMonth(11)
                .setExpireYear(2018)
                .setFirstName("Joe")
                .setLastName("Shopper");
        PaymentAPI.getInstance().setCreditCard(creditCard);
        Payment payment = PaymentAPI.getInstance().createPayment(new Amount("USD", "12"), "test direct payment with credit card", "sale");
        assertThat(payment.getState(), is("approved"));
    }

    @Test
    public void testCreatePaymentMastercard() throws PayPalRESTException {
        CreditCard creditCard = new CreditCard()
                .setType("mastercard")
                .setNumber("5500005555555559")
                .setExpireMonth(12)
                .setExpireYear(2018)
                .setFirstName("Batsy")
                .setLastName("Buyer");
        PaymentAPI.getInstance().setCreditCard(creditCard);
        Payment payment = PaymentAPI.getInstance().createPayment(new Amount("USD", "12"), "test direct payment with credit card", "sale");
        assertThat(payment.getState(), is("approved"));
    }
}
