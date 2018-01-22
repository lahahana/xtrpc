package com.github.lahahana.xtrpc.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple<K, V> {

    private K k;

    private V v;
}
