package com.github.mattisonchao.node

import com.github.mattisonchao.option.StorageOptions

interface Options {

    fun withStorageOptions(storageOption: StorageOptions): Node

}