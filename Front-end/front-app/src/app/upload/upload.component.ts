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
  selectedFiles: File[] = [];  // Armazena os arquivos selecionados
  dadosExtraidos: any[] = [];  // Dados extraídos 
  dataId: string = '';  // ID dos dados extraídos
  carregando = false;  // Indica se o upload está em progresso
  erro = '';  // Mensagens de erro
  mensagem: string = '';  // Mensagens gerais
  isDragging = false;  // Flag para arrastar arquivos

  constructor(private geminiService: GeminiService) {}

  // Manipula a seleção de arquivos
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.addFiles(Array.from(input.files));
      input.value = '';  // Permite reenviar o mesmo arquivo
    }
  }

  // Atualiza a flag ao arrastar o arquivo
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  // Reseta a flag quando o arquivo sai da área de arraste
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
  }

  // Adiciona arquivos ao selecionar ou arrastar
  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files) {
      this.addFiles(Array.from(event.dataTransfer.files));
    }
  }

  // Adiciona novos arquivos, evitando duplicados
  addFiles(novosArquivos: File[]): void {
    const ignorados: string[] = [];
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
      setTimeout(() => this.mensagem = '', 3000);
    }
  }

  // Remove arquivo da lista
  removerArquivo(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  // Envia arquivos para o servidor
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
      error: () => {
        this.erro = 'Erro ao processar as imagens.';
        this.carregando = false;
      }
    });
  }

  // Limpa os dados e estados
  limparTela(): void {
    this.selectedFiles = [];
    this.dadosExtraidos = [];
    this.dataId = '';
    this.erro = '';
    this.mensagem = '';
  }
}
