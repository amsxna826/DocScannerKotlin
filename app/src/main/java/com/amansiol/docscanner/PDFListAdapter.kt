package com.amansiol.docscanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

class PDFListAdapter(var context: Context) : RecyclerView.Adapter<PDFListAdapter.PDFListViewHolder>() {

    companion object {
        var fileToBeDeleted: File? = null
    }

    private fun sharePDF(file: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val pdfUri: Uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file);
        sharingIntent.type = "application/pdf"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share PDF using"))
    }

    private fun deleteFile(file: File) {
        file.delete()
        ViewAllPdf.pdfArray.remove(file)
        ViewAllPdf.pdfListAdapter.notifyDataSetChanged()
        Toast.makeText(context,"${file.name} deleted",Toast.LENGTH_SHORT).show()
    }

    override fun onBindViewHolder(holder: PDFListViewHolder, position: Int) {
        val file: File = ViewAllPdf.pdfArray[position]
        holder.fileNameTextView.text = file.name

        //Get the last modified date of the PDF file (creation date cannot be fetched)
        val date: Date = Date(file.lastModified())
        holder.fileCreationDate.text = date.toString()

        //Allow sharing of PDF when shareImageView is pressed
        holder.shareImageView.setOnClickListener(View.OnClickListener {
            sharePDF(file)
        })

        //delete PDF
        holder.deleteImageView.setOnClickListener(View.OnClickListener {
            fileToBeDeleted = file
            //Check whether we have the permission to delete the files on device by checking write external storage permission
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                deleteFile(file)
            } else {
                //Ask for permission
                ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),Constants.RC_WRITE_EXTERNAL_STORAGE)
            }
        })

        //To edit PDF
        holder.editImageView.setOnClickListener(View.OnClickListener {
            //to be implemented
            fileToBeDeleted = null
            Toast.makeText(context,"Edit toast",Toast.LENGTH_SHORT).show()
        })

        //In general onClick listener on a listItem to show the PDF
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context,PdfViewActivity::class.java)
            intent.putExtra(Constants.INTENT_POSITION_KEY_NAME, position)
            intent.putExtra(Constants.IS_FROM_DEVICE,true)
            context.startActivity(intent)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFListViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recent_pdf_row, parent,false)
        return PDFListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ViewAllPdf.pdfArray.size
    }

    class PDFListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name)
        val fileCreationDate: TextView = itemView.findViewById(R.id.file_date)
        val fileThumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val deleteImageView: ImageView = itemView.findViewById(R.id.file_del)
        val shareImageView: ImageView = itemView.findViewById(R.id.file_share)
        val editImageView: ImageView = itemView.findViewById(R.id.file_edit_name)
    }
}