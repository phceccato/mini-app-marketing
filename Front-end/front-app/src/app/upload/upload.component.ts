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
  mensagem: string = '';
  isDragging = false;

  constructor(private geminiService: GeminiService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.addFiles(Array.from(input.files));
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files) {
      this.addFiles(Array.from(event.dataTransfer.files));
    }
  }

  addFiles(novosArquivos: File[]): void {
    let arquivoDuplicado = false;

    for (const novo of novosArquivos) {
      const jaExiste = this.selectedFiles.some(f => f.name === novo.name);
      if (jaExiste) {
        arquivoDuplicado = true;
      } else {
        this.selectedFiles.push(novo);
      }
    }

    this.mensagem = arquivoDuplicado ? 'Alguns arquivos já foram selecionados e foram ignorados.' : '';
  }

  removerArquivo(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  enviarArquivos(): void {
    if (this.selectedFiles.length === 0) return;

    this.carregando = true;
    this.erro = '';
    this.mensagem = '';

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

  limparTela(): void {
    this.selectedFiles = [];
    this.dadosExtraidos = [];
    this.dataId = '';
    this.erro = '';
    this.mensagem = '';
  }
}
