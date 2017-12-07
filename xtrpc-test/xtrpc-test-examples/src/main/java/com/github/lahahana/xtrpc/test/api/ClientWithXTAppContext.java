package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.client.ref.XTClientApplicationContext;
import com.github.lahahana.xtrpc.common.config.DirectRefService;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.test.service.AddressService;

public class ClientWithXTAppContext {

    static String address = NetworkUtil.getLocalHostInetAddress().getHostAddress() +":" + 8088;

    public static void main(String[] args) throws Exception {
        XTClientApplicationContext context = new  XTClientApplicationContext.Builder()
                                    .addDirectRefService(new DirectRefService(AddressService.class, "xt", address))
                                    .build();
        context.start();
        AddressService addressService = context.getRefService(AddressService.class);
        System.out.println(addressService.getAddressByUserId(10));

    }
}
