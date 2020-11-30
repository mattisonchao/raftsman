package com.github.mattisonchao.node

import com.github.mattisonchao.option.NodeOptions
import com.github.mattisonchao.option.StorageOptions

interface Options {

    fun withStorageOptions(storageOption: StorageOptions): Node

    fun withNodeOptions(nodeOptions: NodeOptions): Node
}