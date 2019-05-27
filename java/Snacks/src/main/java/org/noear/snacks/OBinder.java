package org.noear.snacks;

import java.io.Serializable;

public interface OBinder extends Serializable {
    void bind(ONode data);
}

