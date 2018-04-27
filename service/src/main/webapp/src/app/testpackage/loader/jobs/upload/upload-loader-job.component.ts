import {Component, ElementRef, Input, OnInit, ViewChild} from "@angular/core";
import {FileItem, FileUploader, ParsedResponseHeaders} from "ng2-file-upload";
import {TestPackageJob} from "../model/test-package-job.model";
import {Router} from "@angular/router";

@Component({
  selector: 'upload-loader-job',
  styleUrls: ['./upload-load-job.component.less'],
  templateUrl: './upload-loader-job.component.html'
})
export class UploadLoaderJobComponent implements OnInit  {
  serviceUrl = 'api/load';
  uploader: FileUploader;
  hasDropZoneOver = false;
  skipArt: boolean = false;
  skipScoring: boolean = false;
  @ViewChild("fileDialog") fileDialog: ElementRef;
  @Input() isReadOnly: boolean;

  constructor(private router: Router) {
  }

  ngOnInit() {
    this.uploader = new FileUploader({
      url: this.serviceUrl
    });
    this.uploader.setOptions({autoUpload: false});
    this.uploader.isUploading = false;

    this.uploader.onSuccessItem = (item: FileItem, response: string, status: number, headers: ParsedResponseHeaders) => {
      console.log('Successfully uploaded: ' + item.file.name);
    };

    this.uploader.onErrorItem = (item: FileItem, response: string, status: number, headers: ParsedResponseHeaders) => {
      console.log('onErrorItem status: ' + status);
      this.uploader.clearQueue();
    };

    this.uploader.onBeforeUploadItem = (fileItem: FileItem) => {
      console.log('Uploading ' + fileItem.file.name);
    };

    this.uploader.onCompleteAll = () => {
      this.uploader.clearQueue();
      this.router.navigateByUrl('/loader');
    };
  }

  fileOverBase(e: any): void {
    this.hasDropZoneOver = e;
  }

  openFileDialog() {
    this.fileDialog.nativeElement.click();
  }

  onSubmit() {
    this.uploader.options.additionalParameter =  {
      'skipArt': this.skipArt,
      'skipScoring': this.skipScoring
    };
    this.uploader.uploadAll();
  }

  onCancel() {
    this.uploader.clearQueue();
    this.router.navigateByUrl('/loader');
  }
}
