@file:Suppress("UNCHECKED_CAST")

package tests

import Generators
import PermutationScope
import TestCase
import okio.ByteString.Companion.toByteString
import oneOf
import org.thoughtcrime.securesms.backup.v2.proto.*

/**
 * Incoming/outgoing payment update messages.
 */
object ChatItemPaymentNotificationTestCase : TestCase("chat_item_payment_notification") {
  override fun PermutationScope.execute() {
    frames += StandardFrames.MANDATORY_FRAMES

    frames += StandardFrames.recipientAlice
    frames += StandardFrames.chatAlice

    val (incomingGenerator, outgoingGenerator) = Generators.incomingOutgoingDetails(StandardFrames.recipientAlice.recipient!!)

    val incoming = some(incomingGenerator)
    val outgoing = some(outgoingGenerator)

    val (transactionGenerator, failedTransactionGenerator) = oneOf(
      Generators.permutation {
        val publicKey = listOf(some(Generators.bytes(32)).toByteString())
        val keyImages = listOf(some(Generators.bytes(32)).toByteString())

        frames += PaymentNotification.TransactionDetails.Transaction(
          status = someEnum(PaymentNotification.TransactionDetails.Transaction.Status::class.java),
          mobileCoinIdentification = PaymentNotification.TransactionDetails.MobileCoinTxoIdentification(
            publicKey = publicKey.takeIf { incoming != null } ?: emptyList(),
            keyImages = keyImages.takeIf { outgoing != null } ?: emptyList()
          ),
          timestamp = someIncrementingTimestamp(),
          blockIndex = somePositiveLong(),
          blockTimestamp = someTimestamp(),
          transaction = someBytes(32).toByteString(),
          receipt = someBytes(32).toByteString()
        )
      },
      Generators.permutation {
        frames += PaymentNotification.TransactionDetails.FailedTransaction(
          reason = someEnum(PaymentNotification.TransactionDetails.FailedTransaction.FailureReason::class.java)
        )
      }
    )

    frames += Frame(
      chatItem = ChatItem(
        chatId = StandardFrames.chatAlice.chat!!.id,
        authorId = if (outgoing != null) {
          StandardFrames.recipientSelf.recipient!!.id
        } else {
          StandardFrames.recipientAlice.recipient!!.id
        },
        dateSent = someIncrementingTimestamp(),
        incoming = incoming,
        outgoing = outgoing,
        paymentNotification = PaymentNotification(
          amountMob = some(Generators.picoMobs()),
          feeMob = some(Generators.picoMobs()),
          note = someNullableString(),
          transactionDetails = PaymentNotification.TransactionDetails(
            transaction = someOneOf(transactionGenerator),
            failedTransaction = someOneOf(failedTransactionGenerator)
          )
        )
      )
    )
  }
}
