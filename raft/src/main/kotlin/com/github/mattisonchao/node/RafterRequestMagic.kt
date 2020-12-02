package com.github.mattisonchao.node

/**
 * Rafter rpc request type.
 *
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 * @property code rpc request type code
 */
enum class RafterRequestMagic(val code: Int) {
    VOTE(0),
    APPEND_ENTRIES(1),
    CLIENT_REQUEST(2);

    companion object {
        /**
         * Get Rafter request type enum by code
         *
         * @param code rpc request type code
         * @return rafter request type or not
         * @throws IllegalArgumentException
         */
        fun valueOf(code: Int): RafterRequestMagic {
            return RafterRequestMagic.values().find { it.code == code }
                    ?: throw IllegalArgumentException("Rafter request type can not parse code $code")
        }
    }
}