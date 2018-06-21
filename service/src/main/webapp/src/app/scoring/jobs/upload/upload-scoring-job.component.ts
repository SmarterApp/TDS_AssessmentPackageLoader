import {Component, ElementRef, Input, OnInit, ViewChild} from "@angular/core";
import {FileItem, FileUploader, ParsedResponseHeaders} from "ng2-file-upload";
import {Router} from "@angular/router";

@Component({
  selector: 'upload-scoring-job',
  styleUrls: ['./upload-scoring-job.component.scss'],
  templateUrl: './upload-scoring-job.component.html'
})
export class UploadScoringJobComponent implements OnInit  {
  serviceUrl = 'api/scoring';
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
      this.router.navigateByUrl('/scoring');
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
    this.router.navigateByUrl('/scoring');
  }
}
