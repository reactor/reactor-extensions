package reactor.bool

import reactor.core.publisher.Mono

/**
 * Extension to logically revert a [Boolean] [Mono]. It can also be
 * applied as a ! operator.
 *
 * @author Simon Baslé
 * @since 3.2.0
 */
operator fun Mono<Boolean>.not(): Mono<Boolean> = BooleanUtils.not(this)

/**
 * Extension to logically combine two [Boolean] [Mono] with the AND operator.
 *
 * @author Simon Baslé
 * @since 3.2.0
 */
fun Mono<Boolean>.andBoolean(rightHand: Mono<Boolean>): Mono<Boolean> = BooleanUtils.and(this, rightHand)

/**
 * Extension to logically combine two [Boolean] [Mono] with the Not-AND (NAND) operator.
 *
 * @author Simon Baslé
 * @since 3.2.0
 */
fun Mono<Boolean>.nand(rightHand: Mono<Boolean>): Mono<Boolean> = BooleanUtils.nand(this, rightHand)

/**
 * Extension to logically combine two [Boolean] [Mono] with the OR operator.
 *
 * @author Simon Baslé
 * @since 3.2.0
 */
fun Mono<Boolean>.orBoolean(rightHand: Mono<Boolean>): Mono<Boolean> = BooleanUtils.or(this, rightHand)

/**
 * Extension to logically combine two [Boolean] [Mono] with the Not-OR (NOR) operator.
 *
 * @author Simon Baslé
 * @since 3.2.0
 */
fun Mono<Boolean>.nor(rightHand: Mono<Boolean>): Mono<Boolean> = BooleanUtils.nor(this, rightHand)

/**
 * Extension to logically combine two [Boolean] [Mono] with the exclusive-OR (XOR) operator.
 *
 * @author Simon Baslé
 * @since 3.2.0
 */
fun Mono<Boolean>.xor(rightHand: Mono<Boolean>): Mono<Boolean> = BooleanUtils.xor(this, rightHand)