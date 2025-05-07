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
      input.value = ''; // 🔧 permite reenviar o mesmo arquivo
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
    const ignorados: string[] = [];

    // Combina nome, tamanho e data de modificação para evitar falsos positivos
    const arquivosExistentes = new Set(
      this.selectedFiles.map(f => `${f.name}-${f.size}-${f.lastModified}`)
    );

    for (const novo of novosArquivos) {
      const chave = `${novo.name}-${novo.size}-${novo.lastModified}`;

      if (arquivosExistentes.has(chave)) {
        ignorados.push(novo.name);
      } else {
        this.selectedFiles.push(novo);
        arquivosExistentes.add(chave);
      }
    }

    if (ignorados.length > 0) {
      this.mensagem = `Arquivo(s) já selecionado(s): ${ignorados.join(', ')}`;
      setTimeout(() => {
        this.mensagem = '';
      }, 3000);
    } else {
      this.mensagem = '';
    }
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