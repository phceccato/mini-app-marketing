import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GeminiService } from '../services/gemini.service';
import { ReviewComponent } from '../review/review.component';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule, FormsModule, ReviewComponent],
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent {
  selectedFiles: File[] = [];
  dadosExtraidos: any[] = [];
  dataId: string = '';
  carregando = false;
  erro = '';

  constructor(private geminiService: GeminiService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const novosArquivos = Array.from(input.files);
      this.selectedFiles.push(...novosArquivos);

    }
  }

  enviarArquivos(): void {
    if (this.selectedFiles.length === 0) return;

    this.carregando = true;
    this.erro = '';

    this.geminiService.uploadImages(this.selectedFiles).subscribe({
      next: (res) => {
        this.dadosExtraidos = res.data;
        this.dataId = res.dataId;
        this.carregando = false;
      },
      error: (err) => {
        this.erro = 'Erro ao processar as imagens.';
        this.carregando = false;
      }
    });
  }
}
