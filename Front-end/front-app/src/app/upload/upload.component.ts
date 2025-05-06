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

  constructor(private geminiService: GeminiService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const novosArquivos = Array.from(input.files);
      const arquivosRepetidos: string[] = [];
      const nomesJaSelecionados = new Set(this.selectedFiles.map(f => f.name));
      const nomesAdicionadosNoMesmoEnvio = new Set<string>();
  
      for (const novo of novosArquivos) {
        const nome = novo.name;
  
        const duplicado =
          nomesJaSelecionados.has(nome) || nomesAdicionadosNoMesmoEnvio.has(nome);
  
        if (duplicado) {
          arquivosRepetidos.push(nome);
        } else {
          this.selectedFiles.push(novo);
          nomesAdicionadosNoMesmoEnvio.add(nome);
        }
      }
  
      if (arquivosRepetidos.length > 0) {
        this.mensagem = `Arquivo já selecionado: ${arquivosRepetidos.join(', ')}`;
        setTimeout(() => {
          this.mensagem = '';
        }, 3000);
      } else {
        this.mensagem = '';
      }
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

  removerArquivo(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  limparTela(): void {
    this.selectedFiles = [];
    this.dadosExtraidos = [];
    this.dataId = '';
    this.mensagem = '';
    this.erro = '';
    this.carregando = false;
  }  
}


