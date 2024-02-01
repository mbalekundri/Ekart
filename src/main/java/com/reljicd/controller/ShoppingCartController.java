package com.reljicd.controller;

import com.reljicd.exception.NotEnoughProductsInStockException;
import com.reljicd.service.ProductService;
import com.reljicd.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    private final ProductService productService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, ProductService productService) {
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
    }

    @GetMapping("/shoppingCart")
    public ModelAndView shoppingCart() {
        ModelAndView modelAndView = new ModelAndView("/shoppingCart");
        modelAndView.addObject("products", shoppingCartService.getProductsInCart());
        modelAndView.addObject("total", shoppingCartService.getTotal().toString());
        return modelAndView;
    }

    @GetMapping("/shoppingCart/addProduct/{productId}")
    public String addProductToCart(@PathVariable("productId") Long productId) {
        productService.findById(productId).ifPresent(shoppingCartService::addProduct);
        return "redirect:/shoppingCart";
    }

    @GetMapping("/shoppingCart/removeProduct/{productId}")
    public String removeProductFromCart(@PathVariable("productId") Long productId) {
        productService.findById(productId).ifPresent(shoppingCartService::removeProduct);
        return "redirect:/shoppingCart";
    }

    @GetMapping("/shoppingCart/checkout")
    public String checkout() {
        try {
            shoppingCartService.checkout();
        } catch (NotEnoughProductsInStockException e) {
            shoppingCart().addObject("outOfStockMessage", e.getMessage());
        }
        return "redirect:/shoppingCart";
    }
}
