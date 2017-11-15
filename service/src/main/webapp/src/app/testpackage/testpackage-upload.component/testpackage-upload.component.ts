import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {FileItem, FileUploader, ParsedResponseHeaders} from "ng2-file-upload";


@Component({
  selector: 'testpackage-upload-component',
  templateUrl: './testpackage-upload.component.html',
  styleUrls: ['./testpackage-upload.component.less'],
})
export class TestPackageUploadComponent implements OnInit {
  serviceUrl = '/api/load';
  uploader: FileUploader;
  hasDropZoneOver = false;
  @ViewChild("fileDialog") fileDialog: ElementRef;
  @Input() isReadOnly: boolean;
  @Input() isMultiple: boolean;

  constructor() {
  }

  ngOnInit() {
    this.uploader = new FileUploader({url: this.serviceUrl});
    this.uploader.setOptions({autoUpload: false});
    this.uploader.isUploading = false;

    this.uploader.onSuccessItem = (item: FileItem, response: string, status: number, headers: ParsedResponseHeaders) => {
      console.log('Successfully uploaded: ' + item.file.name);
    };

    this.uploader.onErrorItem = (item: FileItem, response: string, status: number, headers: ParsedResponseHeaders) => {
      console.log('onErrorItem status: ' + status);
    };

    this.uploader.onAfterAddingAll = () => {
      if (this.isMultiple === false && this.uploader.queue.length > 1) {
        this.uploader.cancelAll();
        console.log('File Upload Error', 'Only one file at a time may be uploaded for this resource.');
        this.uploader.clearQueue();
        return;
      }
      this.uploader.uploadAll();
    };

    this.uploader.onBeforeUploadItem = (fileItem: FileItem) => {
      console.log('Uploading ' + fileItem.file.name);
    };

    this.uploader.onCompleteAll = () => {
      this.uploader.clearQueue();
    };
  }

  fileOverBase(e: any): void {
    this.hasDropZoneOver = e;
  }

  openFileDialog() {
    this.fileDialog.nativeElement.click();
  }
}
