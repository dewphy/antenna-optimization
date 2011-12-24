clc; clear all; close all;

% Define parameters.
dir = '../results/';
nRuns = 20;
nEvals = 10000;

% Load benchmarks.
B(1).minPosition = [0, 0.5];
B(1).maxPosition = [90, 3];
B(1).optPosition = [35.5, 2.56];
B(1).optFitness = 3.2063;

B(2).minPosition = [0, 5];
B(2).maxPosition = [180, 15];
B(2).optFitness = [18.2810, 18.03, 17.824, 17.66, 17.539, 17.458, 17.378, 17.298, 17.258, 17.179];
B(2).optPosition = [90, 5.9;
                    90, 6.9;
                    90, 7.9;
                    90, 8.9;
                    90, 9.9;
                    90,10.9;
                    90,11.9;
                    90,12.9;
                    90,13.9;
                    90,14.9];

B(3).minPosition = [0, 0];
B(3).maxPosition = [180, 4];
B(3).optFitness = [7.0632, 7.0632, 7.0632];
B(3).optPosition = [90,0.5;
                    90,1.5;
                    90,2.5;
                    90,3.5];

B(4).minPosition = [10, 0.5];
B(4).maxPosition = [90, 1.5];
B(4).optFitness = 5.8210;
B(4).optPosition = [41, 1.5];

accuracy = 0.0001;

% Load algorithm.
A(1).name = 'GHC';
A(2).name = 'RHC';
A(3).name = 'SAHC';
A(4).name = 'SGA';
A(5).name = 'SSGA';
A(6).name = 'ES';
A(7).name = 'SAGA';
A(8).name = 'PSO';
A(9).name = 'ACO';
A(10).name = 'GP';
S = '><osdphx*+';

for b = 1:length(B),
    
    % Initialize plot mean best fitness.
    figure('Name', ['Benchmark' num2str(b)]); hold all;
    map = colormap(jet);
    C = map(round(linspace(1,64,10)),:);
    
    for a = 1:length(A),
        
        bestFitnesses = [];
        bestPosition1 = [];
        bestPosition2 = [];
        sumHitTime = 0;
        nSuccess = 0;
        for i = 1:nRuns,
            D = dlmread([dir 'B' num2str(b) '/' A(a).name '/' num2str(i) '.txt']);
            len = size(D, 1);
            
            if D(len,1) == B(b).optFitness(1),
                nSuccess = nSuccess + 1;
                sumHitTime = sumHitTime + len;
            end
            
            if len > nEvals,
                D = D(1:nEvals, :);
            else
                D((len+1):nEvals,:) = repmat(D(len,:), nEvals-len,1);
            end
            bestFitnesses = [bestFitnesses; D(:,1)'];
            bestPosition1 = [bestPosition1; D(:,2)'];
            bestPosition2 = [bestPosition2; D(:,3)'];
        end
        
        successRate(b,a) = int32(100*nSuccess/nRuns);
        meanHitTime(b,a) = sumHitTime/nSuccess;
        
        display([' ']);
        display(['--------------------------' A(a).name '--------------------------']);
        display(['               Success rate: ' num2str(successRate(b,a)) '%']);
        display(['   Normalized mean hit time: ' num2str(meanHitTime(b,a)) ' evaluation']);
        display([' ']);
        
        % Compute mean best fitness.
        meanBestFitness = mean(bestFitnesses)/B(b).optFitness(1);
        
        % Plot mean best fitness.
        plot(1:50:10000, meanBestFitness(1:50:10000), ['-' S(a)], 'Color', C(a,:), 'MarkerSize', 10);
        
%         % Compute mean genotypic distance.
%         for i = 1:size(bestPosition1,1),
%             for j = 1:size(bestPosition1,2),
%                 for k = 1:length(B(b).optFitness),
%                     allGenDist(k) = 0;
%                     allGenDist(k) = allGenDist(k) + ((bestPosition1(i,j) - B(b).optPosition(k,1)) ./ (B(b).maxPosition(1) - B(b).minPosition(1))).^2;
%                     allGenDist(k) = allGenDist(k) + ((bestPosition2(i,j) - B(b).optPosition(k,2)) ./ (B(b).maxPosition(2) - B(b).minPosition(2))).^2;
%                     allGenDist(k) = allGenDist(k).^(0.5);
%                 end
%                 genDist(i,j) = min(allGenDist);
%             end
%         end
%         meanGenDist = mean(genDist);
%         
%         % Plot mean genotypic distance.
%         figure(2*b); hold on;
%         plot(0.2:0.2:(nEvals/5),meanGenDist, 'Color', C(a,:));
    end
    
    % Finalize plot mean best fitness.
    xlabel('Number of evaluations', 'FontSize', 16);
    ylabel('Mean best fitness', 'FontSize', 16);
    myLegend = legend(A.name, 'Location', 'SouthEast');
    set(myLegend, 'FontSize', 16);
    set(gcf, 'Position', [54, 168, 723, 500]);

    tightInset = get(gca, 'TightInset');
    position(1) = tightInset(1);
    position(2) = tightInset(2);
    position(3) = 1 - tightInset(1) - tightInset(3);
    position(4) = 1 - tightInset(2) - tightInset(4);
    set(gca, 'Position', position);

    grid on;
     set(gca, 'FontSize', 14);
     
    if b == 1,
        xlim([0 1000]);
        ylim([0.8 1]);
    elseif b == 2,
        xlim([0 1000]);
        ylim([0.9 1]);
    elseif b == 3,
        xlim([0 1000]);
        ylim([0.3 1]);
    elseif b == 4,
        xlim([0 1000]);
        ylim([0.8 1]);
    end
    
%     % Finalize plot for mean genotypic distance.
%     figure(2*b); hold on; grid on;
%     xlabel('Normalized Number of Evaluation', 'FontSize', 16);
%     ylabel('Mean Genotypic Distance', 'FontSize', 16);
%     title(['Benchmark' num2str(b)], 'FontSize', 16);
%     legend('GHC', 'RHC', 'SAHC', 'SGA', 'SSGA', 'SAGA', 'ES');
end

figure(length(B)+1); clf;
bar(successRate);
xlabel('Benchmark (#)', 'FontSize', 16);
ylabel('Success rate (%)', 'FontSize', 16);
myLegend = legend(A.name, 'Location', 'SouthEast');
set(gca,'YMinorGrid','on', 'FontSize',14)
set(gcf, 'Position', [59, 367, 1147, 312]);
set(myLegend, 'FontSize', 16, 'Position', [0.900828247602441 0.106570512820513 0.0963382737576286 0.86738782051282]);

figure(length(B)+2); clf;
bar(meanHitTime);
xlabel('Benchmark (#)', 'FontSize', 16);
ylabel('Mean hit time', 'FontSize', 16);
myLegend = legend(A.name, 'Location', 'NorthEast');
set(gca,'YMinorGrid','on', 'FontSize',14)
set(gcf, 'Position', [59, 367, 1147, 312]);
set(myLegend, 'FontSize', 16, 'Position', [0.900828247602441 0.106570512820513 0.0963382737576286 0.86738782051282]);
ylim([0 2200])

annotation(gcf,'textarrow',[0.281624720064197 0.281990521327014],...
    [0.95 0.90],'TextEdgeColor','none',...
    'TextLineWidth',1.1,...
    'FontSize',14,...
    'String',{'3148'},...
    'LineWidth',1.1);

% Create textarrow
annotation(gcf,'textarrow',[0.47274881516588 0.4739336492891],...
    [0.95 0.90],'TextEdgeColor','none',...
    'TextLineWidth',1.1,...
    'FontSize',14,...
    'String',{'4042'},...
    'LineWidth',1.1);