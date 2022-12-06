package com.example.fileexplorer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.fileexplorer.databinding.ActivityMainBinding
import com.example.fileexplorer.treeView.TreeNode
import com.example.fileexplorer.treeView.TreeNodeAdapter
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var nodes = ArrayList<TreeNode>()
    private var adapter: TreeNodeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (permissionCheck()) {

            initTreeView()
        }

        binding.btnExpandAll.setOnClickListener {
            adapter?.expandAll()
        }

        binding.btnCollapseAll.setOnClickListener {
            adapter?.collapseAll()
        }

        binding.btnSelectAll.setOnClickListener {
            adapter?.selectAll()
        }

        binding.btnUnSelectAll.setOnClickListener {
            adapter?.unselectAll()
        }

        binding.btnGetFiles.setOnClickListener {
            val selectedNodes = adapter?.getSelectedNodes()
            val selectedFiles = ArrayList<File>()
            selectedNodes?.forEach { treeNode ->

                // selected files
                if (treeNode.isLeaf()) {
                    System.err.println("selected file name ${treeNode.getFile().name}")
                    selectedFiles.add(treeNode.getFile())
                }
            }

            Toast.makeText(this, "selected file count : ${selectedFiles.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initTreeView() {

        val download = File("/sdcard/Download")
        getTreeNode(download)?.let { nodes.add(it) }

        val picture = File("/sdcard/DCIM")
        getTreeNode(picture)?.let { nodes.add(it) }

        val music = File("/sdcard/Music")
        getTreeNode(music)?.let { nodes.add(it) }

        val movies = File("/sdcard/Movies")
        getTreeNode(movies)?.let { nodes.add(it) }

        adapter = TreeNodeAdapter(nodes)
        binding.rvTreeView.adapter = adapter
    }

    private fun getTreeNode(file: File): TreeNode? {

        if (!file.exists()) return null

        var rootNode = TreeNode(file)

        val fileArray = file.listFiles()
        fileArray?.forEach { child ->

            rootNode = if (child.isDirectory) {
                rootNode.addChild(getChildTreeNodes(child))
            } else {
                rootNode.addChild(TreeNode(child))
            }
        }

        return rootNode
    }

    private fun getChildTreeNodes(file: File): TreeNode {

        var childNode = TreeNode(file)

        val fileArray = file.listFiles()
        fileArray?.forEach { child ->

            childNode = if (child.isDirectory) {
                childNode.addChild(getChildTreeNodes(child))
            } else {
                childNode.addChild(TreeNode(child))
            }
        }

        return childNode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                100 -> initTreeView()
            }
        }
    }

    private fun permissionCheck(): Boolean {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 100
                )
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }
}