package es.onebox.internal.xmlsepa.sepa;

import es.onebox.common.xml.XMLNode;
import es.onebox.internal.xmlsepa.format.SEPAFormatDate;
import es.onebox.internal.xmlsepa.utils.SEPAUtils;

import java.util.Date;
import java.util.List;

public class SEPADirectDebit extends SEPA {

    private String creditorId;

    public SEPADirectDebit(PaymentMethods paymentMethod, SEPABankAccount reciever, List<SEPATransaction> transactions, String creditorId) {
        this(paymentMethod, reciever, transactions, new Date(), creditorId);
    }

    public SEPADirectDebit(PaymentMethods paymentMethod, SEPABankAccount reciever, List<SEPATransaction> transactions, Date executionDate, String creditorId) {
        super(paymentMethod, reciever, transactions, executionDate);
        this.creditorId = creditorId;
        this.build();
    }

    @Override
    protected String getType() {
        return SEPAUtils.NODE_CD;
    }

    @Override
    protected void addTransactions() {
        XMLNode nodeOthr = this.nodePmtInf.append(SEPAUtils.NODE_CDTR_SCHME_ID)
                .append(SEPAUtils.NODE_ID)
                .append(SEPAUtils.NODE_PRVT_ID)
                .append(SEPAUtils.NODE_OTHR);

        nodeOthr.append(SEPAUtils.NODE_ID).value(this.creditorId);
        nodeOthr.append(SEPAUtils.NODE_SCHME_NM)
                .append(SEPAUtils.NODE_PRTRY)
                .value(SEPAUtils.SEPA);

        for (SEPATransaction transaction : this.transactions) {
            XMLNode nodeDrctDbtTxInf = this.nodePmtInf.append(SEPAUtils.NODE_DRCT_DBT_TX_INF);

            XMLNode pmtId = nodeDrctDbtTxInf.append(SEPAUtils.NODE_PMT_ID);
            if (transaction.getId() != null) {
                pmtId.append(SEPAUtils.NODE_INSTR_ID).value(transaction.getId());
            }
            pmtId.append(SEPAUtils.NODE_END_TO_END_ID).value(SEPAFormatDate.formatDateTime(executionDate) + "-" + transaction.getEndToEndId());

            nodeDrctDbtTxInf.append(SEPAUtils.NODE_INSTD_AMT)
                    .attr(SEPAUtils.CCY, transaction.getCurrency().toString())
                    .value(transaction.getValue().doubleValue());

            XMLNode nodeMndtRltdInf = nodeDrctDbtTxInf.append(SEPAUtils.NODE_DRCT_DBT_TX)
                    .append(SEPAUtils.NODE_MNDT_RLTD_INF);
            nodeMndtRltdInf.append(SEPAUtils.NODE_MNDT_ID)
                    .value(transaction.getMandatReference());
            nodeMndtRltdInf.append(SEPAUtils.NODE_DT_OF_SGNTR)
                    .value(SEPAFormatDate.formatDateShort(transaction.getMandatReferenceDate()));

            nodeDrctDbtTxInf.append(SEPAUtils.NODE_DBTR_AGT)
                    .append(SEPAUtils.NODE_FIN_INSTN_ID)
                    .append(SEPAUtils.NODE_BIC)
                    .value(transaction.getBankAccount().getBic());

            XMLNode nodeDbtr = nodeDrctDbtTxInf.append(SEPAUtils.NODE_DBTR);
            nodeDbtr.append(SEPAUtils.NODE_NM).value(transaction.getBankAccount().getName());
            XMLNode nodePstlAdr = nodeDbtr.append(SEPAUtils.NODE_PSTL_ADR);
            nodePstlAdr.append(SEPAUtils.NODE_STRT_NM).value(transaction.getBankAccount().getStreetName());
            nodePstlAdr.append(SEPAUtils.NODE_BLDG_NB).value(transaction.getBankAccount().getBuildingNumber());
            nodePstlAdr.append(SEPAUtils.NODE_PST_CD).value(transaction.getBankAccount().getPostCode());
            nodePstlAdr.append(SEPAUtils.NODE_TWN_NM).value(transaction.getBankAccount().getTownName());
            nodePstlAdr.append(SEPAUtils.NODE_CTRY).value(transaction.getBankAccount().getCountryCode());

            nodeDrctDbtTxInf.append(SEPAUtils.NODE_DBTR_ACCT)
                    .append(SEPAUtils.NODE_ID)
                    .append(SEPAUtils.NODE_IBAN)
                    .value(transaction.getBankAccount().getIban());

            nodeDrctDbtTxInf.append(SEPAUtils.NODE_RMT_INF)
                    .append(SEPAUtils.NODE_USTRD)
                    .value(transaction.getSubject());
        }
    }
}