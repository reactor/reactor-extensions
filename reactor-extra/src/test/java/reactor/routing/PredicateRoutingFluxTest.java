package reactor.routing;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.util.concurrent.QueueSupplier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class PredicateRoutingFluxTest {

    @Test
    public void supportPredicateRouting() {
        PredicateRoutingFlux<Integer, Integer> routingFlux = PredicateRoutingFlux.create(Flux.range(1, 5),
                QueueSupplier.SMALL_BUFFER_SIZE, QueueSupplier.get(QueueSupplier.SMALL_BUFFER_SIZE),
                Function.identity(), true);

        Flux<Integer> evenFlux = routingFlux.route(x -> x % 2 == 0);
        Flux<Integer> oddFlux = routingFlux.route(x -> x % 2 != 0);

        Mono<List<Integer>> evenListMono = evenFlux.collectList().subscribe();
        Mono<List<Integer>> oddListMono = oddFlux.collectList().subscribe();

        assertEquals(Arrays.asList(2, 4), evenListMono.block());
        assertEquals(Arrays.asList(1, 3, 5), oddListMono.block());
    }

    @Test
    public void supportFluentRoutingSyntax() {
        PredicateRoutingFlux<Integer, Integer> routingFlux = PredicateRoutingFlux.create(Flux.range(1, 5),
                QueueSupplier.SMALL_BUFFER_SIZE, QueueSupplier.get(QueueSupplier.SMALL_BUFFER_SIZE),
                Function.identity(), true);

        AtomicReference<Mono<List<Integer>>> evenListMono = new AtomicReference<>();
        AtomicReference<Mono<List<Integer>>> oddListMono = new AtomicReference<>();
        routingFlux.route(x -> x % 2 == 0, flux -> evenListMono.set(flux.collectList().subscribe()))
                .route(x -> x % 2 != 0, flux -> oddListMono.set(flux.collectList().subscribe()));

        assertEquals(Arrays.asList(2, 4), evenListMono.get().block());
        assertEquals(Arrays.asList(1, 3, 5), oddListMono.get().block());
    }

    @Test
    public void fluentRoutingSubscribePartiallyLastUnconsumed() {
        PredicateRoutingFlux<Integer, Integer> routingFlux = PredicateRoutingFlux.create(Flux.range(1, 5),
                QueueSupplier.SMALL_BUFFER_SIZE, QueueSupplier.get(QueueSupplier.SMALL_BUFFER_SIZE),
                Function.identity(), true);

        Flux<Integer> evenFlux = routingFlux.route(x -> x % 2 == 0);
        routingFlux.route(x -> x % 2 != 0).subscribe();

        MonoProcessor<List<Integer>> evenListMono = evenFlux.collectList().subscribe();

        assertEquals(Arrays.asList(2, 4), evenListMono.block());
    }

    @Test
    public void fluentRoutingSubscribePartiallyLastConsumed() {
        PredicateRoutingFlux<Integer, Integer> routingFlux = PredicateRoutingFlux.create(Flux.range(1, 5),
                QueueSupplier.SMALL_BUFFER_SIZE, QueueSupplier.get(QueueSupplier.SMALL_BUFFER_SIZE),
                Function.identity(), true);

        routingFlux.route(x -> x % 2 == 0).log().subscribe();
        Flux<Integer> oddFlux = routingFlux.route(x -> x % 2 != 0).log();

        Mono<List<Integer>> oddListMono = oddFlux.collectList().subscribe();

        assertEquals(Arrays.asList(1, 3, 5), oddListMono.block());
    }

    @Test
    public void supportOtherwiseRouting() {
        PredicateRoutingFlux<Integer, Integer> routingFlux = PredicateRoutingFlux.create(Flux.range(1, 10),
                QueueSupplier.SMALL_BUFFER_SIZE, QueueSupplier.get(QueueSupplier.SMALL_BUFFER_SIZE),
                Function.identity(), true);

        Flux<Integer> remainderZero = routingFlux.route(x -> x % 3 == 0);
        Flux<Integer> remainderOther = routingFlux.routeOtherwise();

        Mono<List<Integer>> remainderZeroMono = remainderZero.collectList().subscribe();
        Mono<List<Integer>> remainderOtherMono = remainderOther.collectList().subscribe();

        assertEquals(Arrays.asList(3, 6, 9), remainderZeroMono.block());
        assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8, 10), remainderOtherMono.block());

    }
}
