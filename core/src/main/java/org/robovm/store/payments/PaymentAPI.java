package org.robovm.store.payments;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.robovm.store.model.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentAPI {

    private static PaymentAPI instance = new PaymentAPI();
    private CreditCard creditCard;
    private User user;

    private PaymentAPI() {
        this.creditCard = new CreditCard();
    }

    public static PaymentAPI getInstance() {
        return instance;
    }

    private String getAccessToken() throws PayPalRESTException {
        InputStream is = PaymentAPI.class.getResourceAsStream("/sdk_config.properties");
        OAuthTokenCredential tokenCredential = Payment.initConfig(is);
        return tokenCredential.getAccessToken();
    }

    public Payment createPayment(Amount amount,
                                 String description,
                                 String intent) throws PayPalRESTException {
        Map<String, String> sdkConfig = new HashMap<>();
        sdkConfig.put("mode", "sandbox");

        APIContext apiContext = new APIContext(getAccessToken());
        apiContext.setConfigurationMap(sdkConfig);

        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(creditCard);

        List<FundingInstrument> fundingInstrumentList = new ArrayList<>();
        fundingInstrumentList.add(fundingInstrument);

        Payer payer = new Payer();
        payer.setFundingInstruments(fundingInstrumentList);
        payer.setPaymentMethod("credit_card");

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        return payment.create(apiContext);
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
