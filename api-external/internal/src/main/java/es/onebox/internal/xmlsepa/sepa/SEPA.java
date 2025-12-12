package es.onebox.internal.xmlsepa.sepa;

import es.onebox.common.xml.XMLNode;
import es.onebox.internal.xmlsepa.format.SEPAFormatDate;
import es.onebox.internal.xmlsepa.utils.SEPAUtils;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class SEPA {
    public enum PaymentMethods {

        DirectDebit(SEPAUtils.DIRECT_DEBIT), Cheque(SEPAUtils.CHEQUE), TransferAdvice(SEPAUtils.TRANSFER_ADVICE),  CreditTransfer(SEPAUtils.CREDIT_TRANSFER);

        private final String code;

        PaymentMethods(String code) {
            this.code = code;
        }
    }

    protected SEPABankAccount reciver;
    protected List<SEPATransaction> transactions;

    protected Date executionDate;

    protected XMLNode document;
    protected XMLNode nodePmtInf;
    protected PaymentMethods paymentMethod;

    public SEPA(PaymentMethods paymentMethod, SEPABankAccount reciver, List<SEPATransaction> transactions) {
        this(paymentMethod, reciver, transactions, new Date());
    }

    public SEPA(PaymentMethods paymentMethod, SEPABankAccount reciver, List<SEPATransaction> transactions, Date executionDate) {
        this.paymentMethod = paymentMethod;
        this.reciver = reciver;
        this.transactions = transactions;
        this.executionDate = executionDate;
    }

    protected void build() {
        this.document = new XMLNode().append(SEPAUtils.NODE_DOCUMENT).attr(SEPAUtils.XMLNS, SEPAUtils.XMLNS_VALUE);

        XMLNode nodeCstmrDrctDbtInitn = this.document.append(SEPAUtils.NODE_CSTMR_DRCT_DBT_INITN);
        XMLNode nodeGrpHdr = nodeCstmrDrctDbtInitn.append(SEPAUtils.NODE_GRP_HDR);

        nodeGrpHdr.append(SEPAUtils.NODE_MSG_ID).value(SEPAFormatDate.formatDateTime(executionDate));
        nodeGrpHdr.append(SEPAUtils.NODE_CRE_DT_TM).value(SEPAFormatDate.formatDateLong(executionDate));
        nodeGrpHdr.append(SEPAUtils.NODE_NB_OF_TXS).value(this.transactions.size());
        nodeGrpHdr.append(SEPAUtils.NODE_CTRL_SUM).value(this.getTransactionVolume().doubleValue());

        XMLNode nodeInitgPty = nodeGrpHdr.append(SEPAUtils.NODE_INITG_PTY);
        nodeInitgPty.append(SEPAUtils.NODE_NM).value(this.reciver.getName());
        XMLNode nodeOthr = nodeInitgPty.append(SEPAUtils.NODE_ID).append(SEPAUtils.NODE_ORG_ID).append(SEPAUtils.NODE_OTHR);
        nodeOthr.append(SEPAUtils.NODE_ID).value(this.reciver.getCreditorId());
        nodeOthr.append(SEPAUtils.NODE_SCHME_NM).append(SEPAUtils.NODE_CD).value(SEPAUtils.CORE);

        this.nodePmtInf = nodeCstmrDrctDbtInitn.append(SEPAUtils.NODE_PMT_INF);
        this.nodePmtInf.append(SEPAUtils.NODE_PMT_INF_ID).value(SEPAUtils.PMT_INF_ID_PREFIX + SEPAFormatDate.formatDate(executionDate));
        this.nodePmtInf.append(SEPAUtils.NODE_PMT_MTD).value(paymentMethod.code);
        this.nodePmtInf.append(SEPAUtils.NODE_NB_OF_TXS).value(this.transactions.size());
        this.nodePmtInf.append(SEPAUtils.NODE_CTRL_SUM).value(this.getTransactionVolume().doubleValue());

        XMLNode nodePmtTpInf = this.nodePmtInf.append(SEPAUtils.NODE_PMT_TP_INF);
        nodePmtTpInf.append(SEPAUtils.NODE_SVC_LVL).append(SEPAUtils.NODE_CD).value(SEPAUtils.SEPA);
        nodePmtTpInf.append(SEPAUtils.NODE_LCL_INSTRM).append(SEPAUtils.NODE_CD).value(SEPAUtils.CORE);
        nodePmtTpInf.append(SEPAUtils.NODE_SEQ_TP).value(SEPAUtils.RCUR);

        this.nodePmtInf.append(SEPAUtils.NODE_REQD_COLLTN_DT).value(SEPAFormatDate.formatDateShort(executionDate));
        this.nodePmtInf.append(this.getType() + SEPAUtils.NODE_TR)
                .append(SEPAUtils.NODE_NM).value(this.reciver.getName());

        this.nodePmtInf.append(this.getType() + SEPAUtils.NODE_TR_ACCT)
                .append(SEPAUtils.NODE_ID)
                .append(SEPAUtils.NODE_IBAN)
                .value(this.reciver.getIban());

        if (this.reciver.getBic() != null) {
            this.nodePmtInf.append(this.getType() + SEPAUtils.NODE_TR_AGT)
                    .append(SEPAUtils.NODE_FIN_INSTN_ID)
                    .append(SEPAUtils.NODE_BIC)
                    .value(this.reciver.getBic());
        }

        this.nodePmtInf.append(SEPAUtils.NODE_CHRG_BR).value(SEPAUtils.CHRG_BR_SLEV);

        this.addTransactions();
    }

    protected abstract String getType();

    protected abstract void addTransactions();

    private BigDecimal getTransactionVolume() {
        BigDecimal volume = BigDecimal.ZERO;
        for (SEPATransaction transaction : this.transactions) {
            volume = volume.add(transaction.getValue());
        }
        return volume;
    }

    public void write(OutputStream outputStream) {
        this.document.write(outputStream);
    }

    public String toString() {
        return this.document.toString();
    }
}