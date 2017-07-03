import {Component, ElementRef, OnInit, AfterViewInit} from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';
import {DomSanitizer} from '@angular/platform-browser';

///https://www.npmjs.com/package/angular2-highlight-js
// import { HighlightJsModule, HighlightJsService } from 'angular2-highlight-js';




import { Account, LoginModalService, Principal } from '../shared';

import {HomeService} from './home.service';

import {
    trigger,
    state,
    style,
    animate,
    transition,
    keyframes,
    group
} from '@angular/animations';
import {CustomizeService} from "../entities/customize/customize.service";
import {Customize} from "../entities/customize/customize.model";
import {Card} from "../entities/card/card.model";
import {AccountService} from "../shared/auth/account.service";
import {Terminal} from "../entities/terminal/terminal.model";
import {Observable} from "rxjs/Observable";
import {TerminalService} from "../entities/terminal/terminal.service";

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.scss'
    ],
    animations: [
        trigger('flyInOut', [
            state('in', style({transform: 'translateX(0)'})),
            state('out', style({opacity: 0, transform: 'translateX(0)',  offset: 1.0})),
            transition('void => *', [
                animate(300, keyframes([
                    style({opacity: 0, transform: 'translateX(-100%)', offset: 0}),
                    style({opacity: 1, transform: 'translateX(15px)',  offset: 0.3}),
                    style({opacity: 1, transform: 'translateX(0)',     offset: 1.0})
                ]))
            ]),
            transition('in => *', [
                animate(300, keyframes([
                    style({opacity: 1, transform: 'translateX(0)',     offset: 0}),
                    style({opacity: 1, transform: 'translateX(-15px)', offset: 0.7}),
                    style({opacity: 0, transform: 'translateX(100%)',  offset: 1.0})
                ]))
            ])
        ])
    ]

})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    cards: Card[][] = [];
    dropdows = [];
    windowRef :any;
    transitions = [];
    terminais: Terminal[] = [];

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: EventManager,
        private homeService: HomeService,
        private customizeService: CustomizeService,
        private accountService: AccountService,
        private domSanitizer: DomSanitizer,
        private terminalService: TerminalService,
    ) {
        this.jhiLanguageService.setLocations(['home']);
        this.windowRef = homeService.getNativeWnidow();
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
        this.registers();
    }

    registers() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
                this.updateCards();
            });
        });

        this.eventManager.subscribe('logout', () =>{
            this.cards = [];
            this.terminais = [];
        });

        this.eventManager.subscribe('customizeListModification', () => {
            this.updateCards();
        });

        this.eventManager.subscribe('cardListModification', () => {
            this.updateCards();
        });

        this.updateCards();
    }

    updateCards() {
        const atualizar: Card[] = [];
        this.customizeService.getCustomize().subscribe(
            (custom: Customize) => {
                if(custom && custom.cenario) {
                    this.homeService.getCards(custom.cenario).subscribe(
                        (cards: Card[][]) => {
                            cards.forEach((cars: Card[], linha: number) => {
                                cars.forEach((card: Card, coluna: number) => {
                                    this.transitions[card.id] = 'in';
                                    let meta: any = this.getMeta(card);
                                    meta = Object.assign(meta,
                                        {endereco: 'http://itgm.mikeias.net:8098/temp/',
                                            enderecoseguro: 'https://itgm.mikeias.net:8099/temp/',
                                            enderecows: 'ws://itgm.mikeias.net:8080/ITGMRest2/jriaccesslive/'
                                        });
                                    card.meta = JSON.stringify(meta);
                                    if (!this.cards[linha] ||
                                        !this.cards[linha][coluna] ||
                                        (JSON.stringify(this.cards[linha][coluna]) !=
                                        JSON.stringify(card))) {
                                        if (!this.cards[linha]) {
                                            this.cards[linha] = [];
                                        }
                                        if (card.linha !== linha || card.coluna !== coluna) {
                                            card.linha = linha;
                                            card.coluna = coluna;
                                            atualizar.push(card);
                                        }
                                        this.cards[linha][coluna] = card;
                                        if (card.modo === 'terminal') {
                                            this
                                                .getTerminal(this.getMeta(card).terminal)
                                                .subscribe((terminal: Terminal) => {
                                                    this.terminais[card.id] = terminal =
                                                        Object.assign(new Terminal(), terminal);
                                                    if (terminal.status === 2) {
                                                        terminal.conectar(JSON.parse(card.meta).enderecows);
                                                    }
                                                });
                                        }
                                    }
                                });
                            });
                        }
                    );
                } else {
                    this.cards = [];
                    this.transitions = [];
                    this.dropdows = [];
                    this.terminais = [];
                }
            }
        );

        atualizar.forEach(card => this.homeService.atualizar(card));

    }


    getTerminal(id: number):Observable<Terminal> {
        return this.terminalService.find(id);
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    getMeta(card: Card): any {
        if(card && card.meta && card.meta.length > 2) {
            try {
                return JSON.parse(card.meta);
            } catch (e) {
                console.log('impossivel obter meta de ' + card.meta + ' error ' + e);
            }
        }
        return {};
    }

    getSize(card: Card):string {
        const meta = this.getMeta(card);
        let tam = -1;
        if (meta) {
            tam = parseInt(meta.size);
            if (tam > (1024 * 1024)) {
                return Math.ceil(tam / (1024 * 1024)) + ' Mb';
            } else if (tam > 1024) {
                return Math.ceil(tam / 1024) + ' Kb';
            }
            return tam + ' bytes';
        }
    }

    toogleDropDown(card: Card) {
        this.closeDropDown();
        this.dropdows[card.id] = this.dropdows[card.id] ? false : true;
    }

    closeDropDown() {
        this.dropdows = [];
    }

    getURL(card: Card): string {
        const meta = this.getMeta(card);
        if (meta) {
            return (card.https ?  meta.enderecoseguro : meta.endereco)  + card.url;
        }
    }

    isResize(card: Card):boolean {
        return ['figura', 'rbokeh', 'texto', 'codigo'].indexOf(card.modo) >= 0;
    }

    getPrevia(text: string):string {
        return '<code>' +
            this.homeService
                .getNativeWnidow()
                .atob(text).split('\n')
                .join('</code><br><code>') + '</code>';
    }

    isArquivo(card: Card):boolean {
        return ['figura','rbokeh','texto','codigo','planilha','rdata'].indexOf(card.modo) >= 0;
    }

///////////////////////////////////////////////////////////////////////////////

    isDestacavel(card: Card):boolean {
        return ['figura', 'rbokeh', 'planilha', 'texto', 'codigo'].indexOf(card.modo) >= 0;
    }

    destacar(card: Card, carde: any) {
        const meta = this.getMeta(card);
        const rect = carde.getBoundingClientRect();
        const url = this.getURL(card);
        const myWindow = window.open(url,
            '_blank',
            'fullscreen=no,' +
            'menubar=no,' +
            'toolbar=no,' +
            'location=yes,' +
            'resizable=yes,' +
            'top=' + (rect.top + 100) + ',' +
            'left=' + rect.left + ',' +
            'height=' + (card.modo === 'figura' && meta !== null ? meta.height: '500') + ',' +
            'width=' + (card.modo === 'figura' && meta !== null ? meta.width: '500')  + ',' +
            'scrollbars=yes,' +
            'status=yes');
        switch (card.modo){
            case 'figura':
                const prefix = '<body style="background-image: url(\'';
                const sufix = '\'); background-repeat: no-repeat; background-size: cover;"></body>';
                myWindow.document.write(prefix + url + sufix);
                break;
            case 'rbokeh':
                break;
            case  'planilha':
                const html = `<!DOCTYPE html>
                        <html lang=en>
                        <head>
                        <meta charset=utf-8 />
                        <title>jQuery CSVToTable</title>
                        <style>TABLE.CSVTable{font:.8em Verdana,Arial,Geneva,Helvetica,sans-serif;border-collapse:collapse;width:450px}TABLE.CSVTable THEAD TR{background:#e8edff}TABLE.CSVTable TH{font-family:"Lucida Sans Unicode","Lucida Grande",Sans-Serif;font-size:1.2em}TABLE.CSVTable TD,TABLE.CSVTable TH{padding:8px;text-align:left;border-bottom:1px solid #fff;border-top:1px solid transparent}TABLE.CSVTable TR{background:#f0f0f0}TABLE.CSVTable TR.odd{background:#f9f9f9}TABLE.CSVTable TR:hover{background:#e8edff}.source{background-color:#fafafa;border:1px solid #999}</style>
                        <script src=https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js></script>
                        <script>(function(a){String.prototype.splitCSV=function(e){for(var d=this.split(e=e||","),b=d.length-1,c;b>=0;b--){if(d[b].replace(/"\s+$/,'"').charAt(d[b].length-1)=='"'){if((c=d[b].replace(/^\s+"/,'"')).length>1&&c.charAt(0)=='"'){d[b]=d[b].replace(/^\s*"|"\s*$/g,"").replace(/""/g,'"')}else{if(b){d.splice(b-1,2,[d[b-1],d[b]].join(e))}else{d=d.shift().split(e).concat(d)}}}else{d[b].replace(/""/g,'"')}}return d};a.fn.CSVToTable=function(c,b){var d={tableClass:"CSVTable",theadClass:"",thClass:"",tbodyClass:"",trClass:"",tdClass:"",loadingImage:"",loadingText:"Loading CSV data...",separator:",",startLine:0};var b=a.extend(d,b);return this.each(function(){var f=a(this);var e="";(b.loadingImage)?loading='<div style="text-align: center"><img alt="'+b.loadingText+'" src="'+b.loadingImage+'" /><br>'+b.loadingText+"</div>":loading=b.loadingText;f.html(loading);a.get(c,function(k){var g='<table class="'+b.tableClass+'">';var i=k.replace("\\r","").split("\\n");if(k.split(";").length>k.split(b.separator).length){b.separator=";"};if(k.split("\\t").length>k.split(b.separator).length){b.separator="\\t"}var h=0;var j=0;var l=new Array();a.each(i,function(n,m){if((n==0)&&(typeof(b.headers)!="undefined")){l=b.headers;j=l.length;g+='<thead class="'+b.theadClass+'"><tr class="'+b.trClass+'">';a.each(l,function(p,q){g+='<th class="'+b.thClass+'">'+q+"</th>"});g+='</tr></thead><tbody class="'+b.tbodyClass+'">'}if((n==b.startLine)&&(typeof(b.headers)=="undefined")){l=m.splitCSV(b.separator);j=l.length;g+='<thead class="'+b.theadClass+'"><tr class="'+b.trClass+'">';a.each(l,function(p,q){g+='<th class="'+b.thClass+'">'+q+"</th>"});g+='</tr></thead><tbody class="'+b.tbodyClass+'">'}else{if(n>=b.startLine){var o=m.splitCSV(b.separator);if(o.length>1){h++;if(o.length!=j){e+="error on line "+n+": Item count ("+o.length+") does not match header count ("+j+") \\n"}(h%2)?oddOrEven="odd":oddOrEven="even";g+='<tr class="'+b.trClass+" "+oddOrEven+'">';a.each(o,function(q,p){g+='<td class="'+b.tdClass+'">'+p+"</td>"});g+="</tr>"}}}});g+="</tbody></table>";if(e){f.html(e)}else{f.fadeOut(500,function(){f.html(g)}).fadeIn(function(){setTimeout(function(){f.trigger("loadComplete")},0)})}})})}})(jQuery);</script>
                        </head>
                        <body>
                        <div id=CSVTable>
                        </div>
                        </body>
                        <script>$("#CSVTable").CSVToTable("url",{loadingImage:"imagemgif",startLine:0});</script>
                        </html>`;

                //não se esqueça de atribuir:
                //<Directory "/var/www/html/temp">
                //Header set Access-Control-Allow-Origin "*"
                //</Directory>
                myWindow.document.write(html.replace('url', url).replace('imagemgif', this.getMeta(card).endereco/*.replace('http:', 'https:').replace(':80', '')*/ + 'loading.gif'));
                break;
            case 'rdata':
            case 'texto':
            case 'codigo':
                //  myWindow.document.write(card.codigo); ///substituir pela url
                break;
            default:
                break;
        }
        this.closeDropDown();
    }

    baixar(card: Card) {
        this.closeDropDown();
    }

    reduzir(card: Card) {
        this.homeService.reduzirCard(card).subscribe((card: Card)=>{
            this.cards[card.linha][card.coluna] = card;
        });
        this.closeDropDown();
    }

    ampliar(card: Card) {
        this.homeService.ampliarCard(card).subscribe((card: Card)=>{
            this.cards[card.linha][card.coluna] = card;
        });
        this.closeDropDown();
    }

    fechar(card: Card) {
        if (card) {
            this.transitions[card.id] = 'out';
            card.tipo = 0;
        }
        this.closeDropDown();
    }

///////////////////////////////////////////////////////////////////////////////

    animationDone($event: any, card: Card) {
        if($event.fromState === 'in' && $event.toState === 'out' && card && card.tipo === 0){
            const  id = card.id;
            this.homeService.removeCard(id).subscribe(
                () => {
                    this.closeDropDown();
                    // this.transitions[id] = null;
                }
            );
        }
    }
}



