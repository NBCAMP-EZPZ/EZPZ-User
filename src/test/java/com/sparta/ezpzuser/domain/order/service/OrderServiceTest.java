package com.sparta.ezpzuser.domain.order.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.cart.service.CartService;
import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.order.repository.OrderRepository;
import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import com.sparta.ezpzuser.domain.orderline.repository.OrderlineRepository;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class OrderServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderlineRepository orderlineRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Order order;
    private Cart cart;
    private Host host;
    private Popup popup;
    private List<Cart> cartList;
    private Item item;
    private Orderline orderline;
    private OrderFindAllResponseDto orderFindAllResponseDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(
                1L,
                "testuser",
                "encodedPassword",
                "Test Name",
                "testuser@example.com",
                "01012345678",
                new ArrayList<>()
        );
        host = new Host(
                1L,
                "hostUsername",
                "hostPassword",
                "host@example.com",
                "Host Company",
                "123-45-67890"
        );
        popup = new Popup(
                1L,                    // id
                host,                  // host
                "Popup Name",          // name
                "Popup Description",   // description
                "http://example.com/thumbnail.jpg", // thumbnailUrl
                "thumbnail.jpg",       // thumbnailName
                "123 Popup St.",       // address
                "Manager Name",        // managerName
                "010-1234-5678",       // phoneNumber
                PopupStatus.IN_PROGRESS,    // popupStatus
                ApprovalStatus.APPROVED, // approvalStatus
                0,                     // likeCount
                LocalDateTime.now(),   // startDate
                LocalDateTime.now().plusDays(7) // endDate
        );
        item = new Item(
                1L,               // id
                popup,            // popup
                "Item Name",      // name
                "Item Description", // description
                1000,             // price
                2,               // stock
                "http://example.com/image.jpg", // imageUrl
                "image.jpg",      // imageName
                0,                // likeCount
                ItemStatus.SALE   // itemStatus
        );

        cart = Cart.of(2, user, item);
        cartList = new ArrayList<>();
        cartList.add(cart);
        order = new Order(1L, 2000, OrderStatus.ORDER_COMPLETED, user, new ArrayList<>());
        orderline = Orderline.of(2, order, item);
        orderFindAllResponseDto = new OrderFindAllResponseDto(
                1L,                    // orderId
                14000,                 // totalPrice
                OrderStatus.ORDER_COMPLETED.toString(), // orderStatus
                "2024-07-24"           // orderDate
        );
        // Set createdAt using reflection
        setCreatedAt(order, LocalDateTime.now());
    }

    private void setCreatedAt(Order order, LocalDateTime createdAt) {
        try {
            java.lang.reflect.Field field = Timestamped.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(order, createdAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test 주문하기")
    public void createOrder() {
        OrderRequestDto requestDto = new OrderRequestDto(
                List.of(new OrderRequestDto.CartIdRequest(1L)));

        when(cartRepository.findAllByIdWithItems(anyList())).thenReturn(cartList);
        doNothing().when(cartService).validateStock(any(Item.class), anyInt());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(itemRepository.save(any(Item.class))).thenReturn(item); // 추가된 코드

        int initialStock = item.getStock();
        int orderQuantity = cart.getQuantity();

        OrderResponseDto responseDto = orderService.createOrder(requestDto, user);

        assertNotNull(responseDto);
        assertEquals(2000, responseDto.getTotalPrice());

        // Item의 stock이 감소했는지 확인
        assertEquals(initialStock - orderQuantity, item.getStock());

        // Item이 품절되어 상태가 바뀌었는지 확인
        assertEquals(ItemStatus.SOLD_OUT, item.getItemStatus());

        // 장바구니가 삭제되었는지 확인
        verify(cartRepository, times(cartList.size())).delete(any(Cart.class));
    }

    @Test
    @DisplayName("Test 주문 목록 조회")
    public void findOrdersAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPages = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(
                orderPages);

        Page<OrderFindAllResponseDto> responsePage = orderService.findOrdersAll(pageable, user);

        assertNotNull(responsePage);
        assertEquals(responsePage.getTotalElements(), 1);
    }

    @Test
    @DisplayName("Test 주문 상세 조회")
    public void findOrder() {
        when(orderRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(
                Optional.of(order));
        when(orderlineRepository.findAllByOrderId(anyLong())).thenReturn(List.of(orderline));

        OrderResponseDto responseDto = orderService.findOrder(1L, user);

        assertNotNull(responseDto);
        assertEquals(responseDto.getOrderId(), 1L);
    }

    @Test
    @DisplayName("Test 주문 취소")
    public void deleteOrder() {
        int initialStock = item.getStock();
        int orderQuantity = orderline.getQuantity();

        when(orderRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(
                Optional.of(order));
        when(orderlineRepository.findAllByOrderId(anyLong())).thenReturn(List.of(orderline));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.deleteOrder(1L, user);

        verify(orderRepository, times(1)).save(order);

        // Item의 stock이 증가했는지 확인
        assertEquals(initialStock + orderQuantity, item.getStock());

        // 주문 상태가 변경되었는지 확인
        assertEquals(order.getOrderStatus(), OrderStatus.CANCELLED);

        // Item이 판매중인지 확인
        assertEquals(item.getItemStatus(), ItemStatus.SALE);
    }

    @Test
    @DisplayName("Test 주문 상세 조회 with ORDER_NOT_FOUND exception")
    public void findOrder_Exception() {
        when(orderRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.findOrder(1L, user));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("Test 주문 취소 with ORDER_NOT_FOUND exception")
    public void deleteOrder_Exception_Order_Not_Found() {
        when(orderRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.deleteOrder(1L, user));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("Test 주문 취소 with ORDER_CANCELLATION_NOT_ALLOWED exception")
    public void deleteOrder_Exception_Order_Cancellation_Not_Allowed() {
        // 이미 취소된 주문이라고 가정
        order.updateStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(
                Optional.of(order));

        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.deleteOrder(1L, user));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.ORDER_CANCELLATION_NOT_ALLOWED);
    }
}
